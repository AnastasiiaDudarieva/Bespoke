package com.bespoke.app.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.bespoke.app.data.model.Equipment
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.Media
import com.bespoke.app.data.model.MediaKind
import com.bespoke.app.data.model.Member
import com.bespoke.app.data.model.PastWorkout
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.model.StreakDataStats
import com.bespoke.app.data.model.Workout
import com.bespoke.app.data.model.requiredWorkoutDays
import com.bespoke.app.data.services.AuthService
import com.bespoke.app.utils.FirebaseStorageUrlCache
import com.bespoke.app.utils.getFirebaseDownloadUrl
import com.bespoke.app.utils.toStartOfDay
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private var equipments: List<Equipment> = emptyList()
        private set

    val completedWorkouts: List<Workout>
        get() = workouts.value
            .filter { it.completedAt != null && it.effort != null }
            .sortedBy { it.completedAt }

    private var memberListener: ListenerRegistration? = null
    private var programsListener: ListenerRegistration? = null
    private var workoutsListener: ListenerRegistration? = null

    private var selectedProgram: Program? = null
    private var selectedExercise: ExerciseEntry? = null
    private var selectedWorkout: Workout? = null

    init {
        currentUserId()?.let { uid ->
            CoroutineScope(Dispatchers.IO).launch {
                loadMemberById(uid)
            }
        }
        loadEquipments()
    }

    fun currentUserId() = auth.currentUserId()

    suspend fun login(email: String, password: String) = auth.login(email, password)
    suspend fun resetPassword(email: String) = auth.resetPassword(email)

    suspend fun loadMemberById(userId: String) {
        try {
            val document = firestore.collection("members").document(userId).get().await()
            val loadedMember = document.toObject(Member::class.java)
            Log.d("MemberRepository", "Loaded member: $loadedMember")
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

    private fun loadEquipments() {
        if (equipments.isNotEmpty())
            return
        FirebaseFirestore.getInstance()
            .collection("equipments")
            .get()
            .addOnSuccessListener { snapshot ->
                equipments = snapshot.documents.mapNotNull {
                    it.toObject(Equipment::class.java)
                }.sortedBy { it.label }
            }
            .addOnFailureListener {
                Log.e("loadEquipments", "Error loading equipments: ${it.message}")
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

    suspend fun updateMemberAvatar(newAvatarUrl: String) {
        val currentUser = auth.currentUserId() ?: return
        val memberRef = firestore.collection("members").document(currentUser)
        try {
            memberRef.update("avatar", newAvatarUrl).await()
            val updatedSnapshot = memberRef.get().await()
            val updatedMember = updatedSnapshot.toObject(Member::class.java)
            _member.emit(updatedMember)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                            Log.e("doc", "${doc}")
                            doc.toObject(Workout::class.java)
                        }
                    _workouts.value = result.sortedBy { workout -> workout.completedAt ?: 0 }
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


    private fun calculatePastWorkouts() {
        val published = programs.value.filter { it.status == Program.Status.PUBLISHED.value }
        val workouts = completedWorkouts
        val pastList = mutableListOf<PastWorkout>()

        for (program in published) {
            val requiredDates = program.requiredWorkoutDays()
            for (date in requiredDates) {
                val didComplete = workouts.any {
                    it.programId == program.id &&
                            Date(it.completedAt!! * 1000L).toStartOfDay() == date.toStartOfDay()
                }

                val calloriesBurned = workouts.filter {
                    it.programId == program.id &&
                            Date(it.completedAt!! * 1000L).toStartOfDay() == date.toStartOfDay()
                }.sumOf { it.caloriesBurned }

                pastList.add(
                    PastWorkout(
                        completedAt = (date.time / 1000L).toInt(),
                        program = program,
                        didComplete = didComplete,
                        calloriesBurned = calloriesBurned
                    )
                )
            }
        }

        _pastWorkouts.value = pastList.sortedBy { it.completedAt }
    }

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
        selectedWorkout = storedWorkout ?: workout
        selectedWorkout?._program?.sections?.flatMap { section -> section.entries.orEmpty() }
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
        if (selectedWorkout != null && selectedWorkout?.id == workoutId)
            return selectedWorkout
        return workouts.value.firstOrNull { it.id == workoutId }
    }
}
