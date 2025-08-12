package com.bespoke.app.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.bespoke.app.data.model.Equipment
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ExerciseEntryInProgress
import com.bespoke.app.data.model.Media
import com.bespoke.app.data.model.MediaKind
import com.bespoke.app.data.model.Member
import com.bespoke.app.data.model.PastWorkout
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.model.Provider
import com.bespoke.app.data.model.StreakDataStats
import com.bespoke.app.data.model.Workout
import com.bespoke.app.data.model.requiredWorkoutDays
import com.bespoke.app.data.services.AuthService
import com.bespoke.app.utils.FirebaseStorageUrlCache
import com.bespoke.app.utils.atStartOfDayEpochSec
import com.bespoke.app.utils.getFirebaseDownloadUrl
import com.bespoke.app.utils.secToLocalDate
import com.bespoke.app.utils.toStartOfDay
import com.bespoke.app.utils.zone
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthService,
) {
    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts

    private val _pastWorkouts = MutableStateFlow<List<PastWorkout>>(emptyList())
    val pastWorkouts: StateFlow<List<PastWorkout>> = _pastWorkouts

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers

    private var equipments: List<Equipment> = emptyList()
        private set

    val completedWorkouts: List<Workout>
        get() = workouts.value
            .filter { it.completedAt != null && it.effort != null }
            .sortedBy { it.completedAt }

    private var memberListener: ListenerRegistration? = null
    private var programsListener: ListenerRegistration? = null
    private var workoutsListener: ListenerRegistration? = null
    private var providersListener: ListenerRegistration? = null

    private var selectedProgram: Program? = null
    private var selectedExercise: ExerciseEntry? = null
    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout

    init {
        currentUserId()?.let { uid ->
            CoroutineScope(Dispatchers.IO).launch {
                loadMemberById(uid)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            loadEquipmentsIfNeeded()
        }
        listenToProviders()
    }

    fun currentUserId() = auth.currentUserId()

    suspend fun login(email: String, password: String) = auth.login(email, password)
    suspend fun resetPassword(email: String) = auth.resetPassword(email)

    suspend fun loadMemberById(userId: String) {
        try {
            val document = firestore.collection("members").document(userId).get().await()
            val loadedMember = document.toObject(Member::class.java)
            _member.value = loadedMember
        } catch (e: Exception) {
            e.printStackTrace()
            _member.value = null
        } finally {
            startListeningForMemberChanges(userId)
            listenToPrograms(userId)
            listenToWorkouts(userId)
            combine(programs, workouts) { _, _ -> }
                .collect {
                    calculatePastWorkouts()
                }
        }
    }


    suspend fun loadEquipmentsIfNeeded() {
        if (equipments.isNotEmpty()) return
        try {
            val snapshot = firestore.collection("equipments").get().await()
            equipments = snapshot.documents.mapNotNull {
                it.toObject(Equipment::class.java)
            }.sortedBy { it.label }
            Log.d("Equipment", "Loaded ${equipments.size} equipments")
        } catch (e: Exception) {
            Log.e("Equipment", "Failed to load equipment", e)
        }
    }

    private fun listenToProviders() {
        providersListener = firestore.collection("providers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error listening to providers", error)
                    return@addSnapshotListener
                }
                val providerList = snapshot?.documents?.mapNotNull { doc ->
                    Log.e("doc", "${doc}")
                    doc.toObject(Provider::class.java)

                } ?: emptyList()

                _providers.value = providerList
            }
    }

    fun getEquipmentLabelById(id: String): String? {
        return equipments.firstOrNull { it.id == id }?.label
    }

    private fun clearMember() {
        _member.value = null
    }

    fun logout() {
        stopListeningForMemberChanges()
        stopListeningForPrograms()
        stopListeningForWorkouts()
        stopListeningForProviders()
        auth.logout()
        clearMember()
    }

    //TODO: Add ime type
    suspend fun uploadProfileImage(bitmap: Bitmap): String? {
        val currentUser = auth.currentUserId() ?: return null
        val storageRef = Firebase.storage.reference
            .child("assets/profiles/${currentUser}.jpg")
        val imageData = bitmap.toJpegByteArray(quality = 100) ?: return null
        storageRef.putBytes(imageData).await()
        return storageRef.downloadUrl.await().toString()
    }


    private fun startListeningForMemberChanges(userId: String) {
        memberListener?.remove()
        val docRef = FirebaseFirestore.getInstance()
            .collection("members")
            .document(userId)
        memberListener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                return@addSnapshotListener
            }
            val updatedMember = snapshot.toObject(Member::class.java)
            _member.value = updatedMember
        }
    }

    private fun listenToPrograms(memberId: String) {
        programsListener?.remove()
        programsListener = firestore.collection("programs")
            .whereArrayContains("memberIds", memberId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MemberRepository", "Error loading programs: ${error.message}")
                }
                if (error != null || snapshot == null) return@addSnapshotListener
                val programList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Program::class.java)
                }
                _programs.value = programList
            }
    }


    private fun listenToWorkouts(memberId: String) {
        workoutsListener?.remove()
        workoutsListener = firestore.collection("members")
            .document(memberId)
            .collection("workouts")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val result =
                        it.documents.mapNotNull { doc ->
                            Log.e("workout", "${doc}")
                            doc.toObject(Workout::class.java)
                        }
                    _workouts.value = result.sortedBy { workout -> workout.startedAt }
                }
            }
    }

    private fun stopListeningForMemberChanges() {
        memberListener?.remove()
        memberListener = null
    }

    private fun stopListeningForPrograms() {
        programsListener?.remove()
        programsListener = null
        _programs.value = emptyList()
    }

    private fun stopListeningForWorkouts() {
        workoutsListener?.remove()
        workoutsListener = null
        _workouts.value = emptyList()
    }

    private fun stopListeningForProviders() {
        providersListener?.remove()
        providersListener = null
        _providers.value = emptyList()
    }

    private fun Bitmap.toJpegByteArray(quality: Int = 100): ByteArray? {
        return try {
            val stream = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            stream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    fun getStreakDataStats(): StreakDataStats? {
        val pastWorkouts = _pastWorkouts.value

        if (pastWorkouts.isEmpty()) return null

        val firstDate = Date(pastWorkouts.first().completedAt * 1000L).toStartOfDay()
        val lastDate = Date(pastWorkouts.last().completedAt * 1000L).toStartOfDay()

        val daysBetween =
            ChronoUnit.DAYS.between(firstDate.toInstant(), lastDate.toInstant()).toInt()

        var longest = 0
        var current = 0
        val dateStats = mutableListOf<StreakDataStats.DateIsComplete>()

        for (i in 0..daysBetween) {
            val date = Calendar.getInstance().apply {
                time = firstDate
                add(Calendar.DAY_OF_YEAR, i)
            }.time

            val workoutsInDay = pastWorkouts.filter {
                Date(it.completedAt * 1000L).toStartOfDay() == date.toStartOfDay()
            }

            if (workoutsInDay.all { it.didComplete }) {
                dateStats.add(StreakDataStats.DateIsComplete(date, true))
                current++
            } else {
                dateStats.add(StreakDataStats.DateIsComplete(date, false))
                current = 0
            }

            longest = maxOf(longest, current)
        }

        return StreakDataStats(dateStats, longestStreak = longest, currentStreak = current)
    }

    private fun isDateInToday(epochSeconds: Int): Boolean {
        val zone = ZoneId.systemDefault()
        val d = Instant.ofEpochSecond(epochSeconds.toLong()).atZone(zone).toLocalDate()
        return d == LocalDate.now(zone)
    }

    private fun calculatePastWorkouts() {
        val published = programs.value.filter { it.status == Program.Status.PUBLISHED.value }
        val workouts = completedWorkouts

        val pastList = mutableListOf<PastWorkout>()

        for (program in published) {
            val requiredDates: List<Date> = program.requiredWorkoutDays()

            for (date in requiredDates) {
                val reqDay = date.toInstant().atZone(zone).toLocalDate()

                val itemsForDay = workouts.filter {
                    it.programId == program.id &&
                            it.completedAt != null &&
                            it.completedAt!!.toLong().secToLocalDate() == reqDay
                }

                pastList += PastWorkout(
                    completedAt = date.atStartOfDayEpochSec(),
                    program = program,
                    didComplete = itemsForDay.isNotEmpty(),
                    caloriesBurned = itemsForDay.sumOf { it.caloriesBurned }
                )
            }
        }

        val programIndex = published.mapIndexed { idx, p -> p.id to idx }.toMap()
        _pastWorkouts.value = pastList.sortedWith(
            compareBy<PastWorkout> { it.completedAt }
                .thenBy { programIndex[it.program.id] ?: Int.MAX_VALUE }
                .thenBy { it.program.id ?: "" }
        )
    }


    val pastWorkoutsWithoutToday: List<PastWorkout>
        get() = _pastWorkouts.value.filter { !isDateInToday(it.completedAt) }


    suspend fun loadExerciseData(program: Program): Program {
        selectedProgram?.let {
            if (it.id == program.id)
                return it
        }
        val exerciseIds = program.sections.flatMap { section ->
            section.entries.orEmpty().mapNotNull { it.exerciseId }
        }.toSet()

        val idToData = mutableMapOf<String, Pair<String?, List<Media>?>>()

        for (id in exerciseIds) {
            try {
                val docSnapshot = firestore.collection("exercises").document(id).get().await()
                val data = docSnapshot.data ?: continue

                val name = data["name"] as? String

                val mediaMap = data["media"] as? Map<*, *>
                val mediaList = mediaMap?.let {
                    try {
                        listOf(
                            Media(
                                id = UUID.randomUUID().toString(),
                                createdAt = (it["createdAt"] as? Number)?.toLong() ?: 0L,
                                kind = MediaKind.fromString(it["kind"] as? String ?: "image")
                                    .toString(),
                                path = it["path"] as? String ?: "",
                                thumbnailPath = it["thumbnailPath"] as? String,
                                squarePath = it["squarePath"] as? String,
                                lengthSec = (it["lengthSec"] as? Number)?.toInt()
                            )
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                idToData[id] = Pair(name, mediaList)

            } catch (_: Exception) {
                // skip
            }
        }

        val updatedSections = program.sections.map { section ->
            val updatedEntries = section.entries?.map { entry ->
                val (name, media) = idToData[entry.exerciseId ?: ""] ?: (null to null)
                entry.copy(name = name, exerciseMedia = media)
            }
            section.copy(entries = updatedEntries)
        }

        val updatedProgram = program.copy(sections = updatedSections)
        selectedProgram = updatedProgram
        return updatedProgram
    }

    fun selectExercise(exercise: ExerciseEntry) {
        selectedExercise = exercise
    }

    fun getSelectedExercise(): ExerciseEntry? {
        return selectedExercise
    }

    suspend fun loadWorkoutData(workout: Workout) {
        val storedWorkout = workouts.value.firstOrNull { it.id == workout.id }
        _selectedWorkout.value = storedWorkout ?: workout
        _selectedWorkout.value?._program?.sections?.flatMap { section -> section.entries.orEmpty() }
            ?.map { entry ->
                val media = entry.exerciseMedia?.firstOrNull { it.kind == "video" }
                media?.path?.let { path ->
                    FirebaseStorageUrlCache.get(path) ?: getFirebaseDownloadUrl(path)?.also {
                        FirebaseStorageUrlCache.set(path, it)
                    }
                }
                media?.thumbnailPath?.let { path ->
                    FirebaseStorageUrlCache.get(path) ?: getFirebaseDownloadUrl(path)?.also {
                        FirebaseStorageUrlCache.set(path, it)
                    }
                }
            }
        selectedProgram = null
    }

    fun getSelectedWorkout(workoutId: String): Workout? {
        val workout = _selectedWorkout.value
        if (workout != null && workout.id == workoutId)
            return workout
        val newWorkout = workouts.value.firstOrNull { it.id == workoutId }
        _selectedWorkout.value = newWorkout
        return newWorkout
    }

    fun getProvider(providerId: String): Provider? {
        return providers.value.firstOrNull { it.id == providerId }
    }

    suspend fun startWorkout(program: Program): Workout {
        val firstEntry = program.sections.firstOrNull()?.entries?.firstOrNull()
            ?: throw IllegalStateException("Program has no entries")

        val exerciseEntryInProgress = ExerciseEntryInProgress(
            currentEntry = firstEntry
        )

        val workout = Workout(
            startedAt = (System.currentTimeMillis() / 1000).toInt(),
            completedExerciseEntries = emptyMap(),
            exerciseEntryInProgress = exerciseEntryInProgress,
            programId = program.id ?: "",
            _program = program
        )

        val memberId = currentUserId()
            ?: throw IllegalStateException("Member not loaded")


        val ref = firestore
            .collection("members")
            .document(memberId)
            .collection("workouts")
            .document()

        val withId = workout.copy(id = ref.id)
        ref.set(withId).await()
        loadWorkoutData(withId)
        return withId
    }

    suspend fun updateWorkout(
        workout: Workout,
        entryInProgress: ExerciseEntryInProgress,
    ) {
        val memberId = currentUserId() ?: throw IllegalStateException("Member not loaded")

        val workoutRef = firestore
            .collection("members")
            .document(memberId)
            .collection("workouts")
            .document(workout.id ?: "")

        val updatedCompletedEntries = workout.completedExerciseEntries.toMutableMap()

        val updatedWorkout = workout.copy(
            exerciseEntryInProgress = entryInProgress,
            completedExerciseEntries = updatedCompletedEntries,
        )

        try {
            workoutRef.set(updatedWorkout, SetOptions.merge()).await()
            Log.d("WorkoutUpdate", "Workout updated successfully via set(merge)")
            _selectedWorkout.value = updatedWorkout
        } catch (e: Exception) {
            Log.e("WorkoutUpdate", "Failed to update workout", e)
        }
    }


    suspend fun updateMemberFields(fields: Map<String, Any?>) {
        val currentUser = auth.currentUserId() ?: return
        val memberRef = firestore.collection("members").document(currentUser)

        try {
            memberRef.update(fields).await()
            val updatedSnapshot = memberRef.get().await()
            Log.e("updatedSnapshot", "${updatedSnapshot}")
            val updatedMember = updatedSnapshot.toObject(Member::class.java)
            _member.emit(updatedMember)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
