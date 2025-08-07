package com.bespoke.app.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ExerciseEntryInProgress
import com.bespoke.app.data.model.ExerciseFeedback
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.data.model.Provider
import com.bespoke.app.data.model.Workout
import com.bespoke.app.data.model.isWorkoutComplete
import com.bespoke.app.data.model.timePerRep
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.utils.FirebaseStorageUrlCache
import com.bespoke.app.utils.getFirebaseDownloadUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    val memberRepository: MemberRepository,
) : ViewModel() {


    private val _currentWorkout = MutableStateFlow<Workout?>(null)
    val currentWorkout: StateFlow<Workout?> = _currentWorkout

    private val _currentExerciseEntry = MutableStateFlow<ExerciseEntry?>(null)
    val currentExerciseEntry: StateFlow<ExerciseEntry?> = _currentExerciseEntry

    private val _exerciseState = MutableStateFlow(ExerciseState.setsStart)
    val exerciseState: StateFlow<ExerciseState> = _exerciseState

    private val _currentSet = MutableStateFlow(1)
    val currentSet: StateFlow<Int> = _currentSet

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _timerValueMillis = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _timerValueMillis
        .map { (it / 1000) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _repCounter = MutableStateFlow(0f)
    val repCount: StateFlow<Int> = combine(
        _repCounter,
        exerciseState
    ) { rawCounter, state ->
        if (state == ExerciseState.active) {
            maxOf(1, rawCounter.toInt() + 1)
        } else {
            0
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _stateText = MutableStateFlow("")
    val stateText: StateFlow<String> = _stateText

    private val _isPausedBtwnRep = MutableStateFlow(false)
    val isPausedBtwnRep: StateFlow<Boolean> = _isPausedBtwnRep

    private val _preActiveCount = MutableStateFlow(0)

    val videoUrl = MutableStateFlow<Uri?>(null)
    val thumbnailUrl = MutableStateFlow<Uri?>(null)

    private val _timeInCurrentRepMillis = MutableStateFlow(0f)
    val timeInCurrentRepMillis: StateFlow<Float> = _timeInCurrentRepMillis

    private var durationJob: Job? = null
    private var countdownJob: Job? = null
    private var timerJob: Job? = null
    private val preActiveSteps = 6
    private val pausePerRepMillis = 1500L

    private val _provider = MutableStateFlow<Provider?>(null)
    val provider: StateFlow<Provider?> = _provider

    fun loadWorkout(workoutId: String) {
        val workout = memberRepository.getSelectedWorkout(workoutId)
        Log.e("workout", "${workout}")
        Log.e("workout counter", "${workout?.exerciseEntryInProgress?.counter}")
        _currentWorkout.value = workout

        _provider.value = workout?._program?.providerId?.let { memberRepository.getProvider(it) }

        val inProgress = workout?.exerciseEntryInProgress
        _currentExerciseEntry.value = inProgress?.currentEntry
        _currentSet.value = inProgress?.currentSet ?: 1
        _isPaused.value = inProgress?.isPaused ?: false
        _isPausedBtwnRep.value = inProgress?.isPausedBtwnRep ?: false

        val frameCount = inProgress?.counter ?: 0
        val totalMillis =  frameCount*(1000/30)
        _timerValueMillis.value = totalMillis
        Log.e("_timerValueMillis", "${_timerValueMillis.value}")

        val fps = 30
        val timePerRepSec = currentExerciseEntry.value!!.timePerRep()
        _repCounter.value = (frameCount / (fps * timePerRepSec)).toFloat()
        val timeIntoCurrentRep = totalMillis % (timePerRepSec*1000)
        Log.e("_repCounter.value", "${_repCounter.value}")
        Log.e("timeIntoCurrentRep", "${timeIntoCurrentRep}")
        _timeInCurrentRepMillis.value = timeIntoCurrentRep.toFloat()

        _exerciseState.value = inProgress?.exerciseState ?: ExerciseState.setsStart

        if (inProgress?.exerciseState != null && inProgress.isPaused == false) {
            startState(inProgress.exerciseState)
        } else {
            if (inProgress?.isPaused == true)
                _stateText.value = ""
            else
                updateStateText()
        }
    }


    fun togglePause() {
        val currentState = _exerciseState.value
        val wasPaused = _isPaused.value
        _isPaused.value = !wasPaused

        if (_isPaused.value) {
            countdownJob?.cancel()
            timerJob?.cancel()
            _stateText.value = "Paused"
        } else {
            _stateText.value = ""
            when (currentState) {
                ExerciseState.setsStart -> startState(ExerciseState.setsStart)
                ExerciseState.preActive -> startPreActiveCountDown()
                ExerciseState.active -> resumeActive()
                ExerciseState.rest -> resumeRest()
                else -> updateStateText()
            }
        }
        updateWorkout()
    }

    fun toNextExercise() {
        val workout = _currentWorkout.value ?: return
        if (workout.isWorkoutComplete()) return
        val allExercises =
            workout._program?.sections?.flatMap { it.entries ?: emptyList() } ?: return
        val current = _currentExerciseEntry.value ?: return
        val currentIndex = allExercises.indexOfFirst { it.id == current.id }

        _isPaused.value = true
        timerJob?.cancel()
        countdownJob?.cancel()
        _exerciseState.value = ExerciseState.setsFinished
        saveSkippedExercise()
        if (currentIndex + 1 < allExercises.size) {
            _currentExerciseEntry.value = allExercises[currentIndex + 1]
            _currentSet.value = 1
            startState(ExerciseState.setsStart)
        } else {
            startState(ExerciseState.setsFinished)
        }
        updateWorkout()
    }

    fun toNextSet() {
        val workout = _currentWorkout.value ?: return
        if (workout.isWorkoutComplete()) return
        val allExercises =
            workout._program?.sections?.flatMap { it.entries ?: emptyList() } ?: return
        val current = _currentExerciseEntry.value ?: return
        val currentIndex = allExercises.indexOfFirst { it.id == current.id }

        _isPaused.value = true
        timerJob?.cancel()
        countdownJob?.cancel()
        if (_currentSet.value < current.sets) {
            saveSkippedSet()
            _currentSet.value += 1
            startState(ExerciseState.setsStart)
        } else if (currentIndex + 1 < allExercises.size) {
            _exerciseState.value = ExerciseState.setsFinished
            saveSkippedSet()
            _currentExerciseEntry.value = allExercises[currentIndex + 1]
            _currentSet.value = 1
            startState(ExerciseState.setsStart)
        } else {
            _exerciseState.value = ExerciseState.setsFinished
            saveSkippedSet()
            startState(ExerciseState.setsFinished)
        }
        updateWorkout()
    }


    private fun resumeActive() {
        val exercise = _currentExerciseEntry.value ?: return
        val basedType = exercise.basedType

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            when (basedType) {
                "Time" -> {
                    val totalTimeSec = exercise.time
                    while (!_isPaused.value && _timerValueMillis.value < totalTimeSec * 1000) {
                        delay(1000)
                        _timerValueMillis.value += 1000
                    }
                    if (!_isPaused.value) {
                        startState(ExerciseState.preRest)
                    }
                }

                "Reps" -> {
                    val reps = exercise.reps
                    val timePerRep = exercise.timePerRep() * 1000 // мс
                    val wasBtwnRep = _isPausedBtwnRep.value

                    val rawProgress = _repCounter.value
                    var currentRep = rawProgress.toInt() + 1
                    var remainingInCurrentRep = ((1f - (rawProgress % 1f)) * timePerRep).toLong()

                    if (wasBtwnRep) {
                        _isPausedBtwnRep.value = true
                        var pauseElapsed = 0L
                        while (pauseElapsed < pausePerRepMillis && !_isPaused.value) {
                            delay(500)
                            pauseElapsed += 500
                        }
                        _isPausedBtwnRep.value = false
                    }

                    while (currentRep <= reps && !_isPaused.value) {
                        var elapsedForThisRep = 0L
                        while (elapsedForThisRep < remainingInCurrentRep && !_isPaused.value) {
                            delay(500)
                            elapsedForThisRep += 500
                            _timerValueMillis.value += 500
                        }
                        if (_isPaused.value) break

                        _repCounter.value = currentRep.toFloat()

                        if (currentRep < reps) {
                            _isPausedBtwnRep.value = true
                            var pauseElapsed = 0L
                            while (pauseElapsed < pausePerRepMillis && !_isPaused.value) {
                                delay(500)
                                pauseElapsed += 500
                            }
                            _isPausedBtwnRep.value = false
                            if (_isPaused.value) break
                        }

                        currentRep++
                        remainingInCurrentRep = timePerRep.toLong()
                    }

                    if (!_isPaused.value && currentRep > reps) {
                        startState(ExerciseState.preRest)
                    }
                }


                else -> {
                    val totalTimeSec = 30
                    while (!_isPaused.value && _timerValueMillis.value < totalTimeSec*1000) {
                        delay(1000)
                        _timerValueMillis.value += 1000
                    }
                    if (!_isPaused.value) {
                        startState(ExerciseState.preRest)
                    }
                }
            }
        }
    }

    private fun resumeRest() {
        val restSeconds = _currentExerciseEntry.value?.rest ?: 30
        _stateText.value = "Recover"

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerValueMillis.value < restSeconds*1000) {
                while (_isPaused.value) delay(100)
                delay(1000)
                _timerValueMillis.value += 1000
            }
            startState(ExerciseState.preActive)
        }
    }


    private fun startState(state: ExerciseState) {
        _exerciseState.value = state
        updateStateText()

        when (state) {
            ExerciseState.setsStart -> startSetsStart()
            ExerciseState.preActive -> startPreActiveState()
            ExerciseState.active -> startActiveState()
            ExerciseState.preRest -> startPreRestState()
            ExerciseState.rest -> startRestState()
            ExerciseState.setsFinished -> startSetsFinishedState()
        }
    }

    private fun startSetsStart() {
        viewModelScope.launch {
            delay(1000)
            startState(ExerciseState.preActive)
        }
    }

    private fun startPreActiveState() = startPreActiveCountDown()

    private fun startActiveState() = startActiveTimer()

    private fun startPreRestState() {
        viewModelScope.launch {
            delay(1000)
            startState(ExerciseState.rest)
        }
    }

    private fun startRestState() {
        _stateText.value = "Recover"
        _timerValueMillis.value = 0
        val current = _currentExerciseEntry.value ?: return
        val workout = _currentWorkout.value
        val allExercises =
            workout?._program?.sections?.flatMap { it.entries ?: emptyList() } ?: return
        val currentIndex = allExercises.indexOfFirst { it.id == current.id }

        if (_currentSet.value < current.sets) {
            _currentSet.value += 1
        } else if (currentIndex + 1 < allExercises.size) {
            _currentExerciseEntry.value = allExercises[currentIndex + 1]
            _currentSet.value = 1
        } else {
            startState(ExerciseState.setsFinished)
            saveFinishedSet()
            updateWorkout()
            return
        }
        val restSeconds = _currentExerciseEntry.value?.rest ?: 3
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerValueMillis.value < restSeconds*1000) {
                while (_isPaused.value) delay(100)
                delay(1000)
                _timerValueMillis.value += 1000
            }
            startState(ExerciseState.preActive)
        }
    }

    private fun startSetsFinishedState() {
        _stateText.value = "Complete!"
    }

    private fun updateStateText() {
        if (_isPaused.value) {
            _stateText.value = "Paused"
            return
        }

        _stateText.value = when (_exerciseState.value) {
            ExerciseState.setsStart -> ""
            ExerciseState.rest -> "Recover"
            ExerciseState.setsFinished -> "Complete!"
            else -> ""
        }
    }

    private fun getPreActiveText(count: Int): String = when (count) {
        0 -> "Get Ready!"
        1 -> "Set ${_currentSet.value}"
        2 -> "3"
        3 -> "2"
        4 -> "1"
        5 -> "Go"
        else -> ""
    }

    private fun startPreActiveCountDown() {
        countdownJob?.cancel()
        _preActiveCount.value = 0

        countdownJob = viewModelScope.launch {
            while (_preActiveCount.value <= preActiveSteps) {
                while (_isPaused.value) delay(100)
                _stateText.value = getPreActiveText(_preActiveCount.value)
                if (_preActiveCount.value == preActiveSteps) {
                    startState(ExerciseState.active)
                    break
                }
                delay(1000)
                _preActiveCount.value += 1
            }
        }
    }

    private fun startActiveTimer() {
        timerJob?.cancel()
        _timerValueMillis.value = 0
        _repCounter.value = 0f
        resumeActive()


    }

    fun loadMediaUrlsIfNeeded() {
        val media =
            currentExerciseEntry.value?.exerciseMedia?.firstOrNull { it.kind == "video" } ?: return
        val videoPath = media.path ?: return
        val thumbPath = media.thumbnailPath ?: return

        viewModelScope.launch {
            thumbnailUrl.value = loadCachedOrDownloadUrl(thumbPath)
            videoUrl.value = loadCachedOrDownloadUrl(videoPath)
        }
    }

    private suspend fun loadCachedOrDownloadUrl(path: String): Uri? {
        return FirebaseStorageUrlCache.get(path) ?: getFirebaseDownloadUrl(path)?.also {
            FirebaseStorageUrlCache.set(path, it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        countdownJob?.cancel()
        durationJob?.cancel()
    }

    fun selectExercise(currentExercise: ExerciseEntry) =
        memberRepository.selectExercise(currentExercise)

    private fun saveFinishedSet() {
        val workout = _currentWorkout.value ?: return
        val exerciseId = _currentExerciseEntry.value?.id ?: return
        val currentSet = _currentSet.value

        val feedback = workout.completedExerciseEntries[exerciseId] ?: ExerciseFeedback()
        val updatedFinishedSets = feedback.finishedSets.toMutableSet()
        updatedFinishedSets.add(currentSet)

        val updatedFeedback = feedback.copy(finishedSets = updatedFinishedSets.toList())
        updateExerciseFeedback(exerciseId, updatedFeedback)
    }

    private fun saveSkippedSet() {
        val workout = _currentWorkout.value ?: return
        val exerciseId = _currentExerciseEntry.value?.id ?: return
        val currentSet = _currentSet.value
        val feedback = workout.completedExerciseEntries[exerciseId] ?: ExerciseFeedback()
        val updatedSkippedSets = feedback.skipedSets.toMutableSet()
        updatedSkippedSets.add(currentSet)
        val updatedFeedback = feedback.copy(skipedSets = updatedSkippedSets.toList())
        updateExerciseFeedback(exerciseId, updatedFeedback)
    }

    private fun saveSkippedExercise() {
        val workout = _currentWorkout.value ?: return
        val entry = _currentExerciseEntry.value ?: return
        val exerciseId = entry.id ?: ""
        val currentSet = _currentSet.value

        val feedback = workout.completedExerciseEntries[exerciseId] ?: ExerciseFeedback()
        val skippedSets = feedback.skipedSets.toMutableSet()

        if (!skippedSets.contains(currentSet))
            skippedSets.add(currentSet)

        if (currentSet < entry.sets)
            skippedSets.addAll((currentSet + 1)..entry.sets)

        val updatedFeedback = feedback.copy(skipedSets = skippedSets.toList())
        updateExerciseFeedback(exerciseId, updatedFeedback)
    }

    private fun updateExerciseFeedback(
        exerciseId: String,
        updatedFeedback: ExerciseFeedback,
    ) {
        val workout = _currentWorkout.value ?: return
        val updatedEntries = workout.completedExerciseEntries.toMutableMap()
        updatedEntries[exerciseId] = updatedFeedback.copy(status = _exerciseState.value.name)

        _currentWorkout.value = workout.copy(completedExerciseEntries = updatedEntries)
    }


    fun updateWorkout() {
        if (_currentWorkout.value == null) return
        Log.e("updateWorkout exerciseDuration", "${_timerValueMillis.value}")
        val exerciseDurationFrames =(_timerValueMillis.value*(30f/1000f)).toInt()
        viewModelScope.launch {
            val updatedEntryInProgress = ExerciseEntryInProgress(
                currentEntry = currentExerciseEntry.value,
                exerciseState = exerciseState.value,
                currentSet = currentSet.value,
                isPaused = isPaused.value,
                isPausedBtwnRep = isPausedBtwnRep.value,
                counter = exerciseDurationFrames
            )
            Log.e("updatedEntryInProgress", "$exerciseDurationFrames")
            currentWorkout.value?.let { memberRepository.updateWorkout(it, updatedEntryInProgress) }
        }
    }
}