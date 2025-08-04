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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min


@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    val memberRepository: MemberRepository,
) : ViewModel() {


    private val _currentWorkout = MutableStateFlow<Workout?>(null)
    private val currentWorkout: StateFlow<Workout?> = _currentWorkout

    private val _currentExerciseEntry = MutableStateFlow<ExerciseEntry?>(null)
    val currentExerciseEntry: StateFlow<ExerciseEntry?> = _currentExerciseEntry

    private val _exerciseState = MutableStateFlow(ExerciseState.setsStart)
    val exerciseState: StateFlow<ExerciseState> = _exerciseState

    private val _currentSet = MutableStateFlow(1)
    val currentSet: StateFlow<Int> = _currentSet

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _timerValue = MutableStateFlow(0)
    private val timerValue: StateFlow<Int> = _timerValue

    private val _counter = MutableStateFlow(0f)
    private val counter: StateFlow<Float> = _counter

    private val _stateText = MutableStateFlow("")
    val stateText: StateFlow<String> = _stateText

    private val _isPausedBtwnRep = MutableStateFlow(false)
    val isPausedBtwnRep: StateFlow<Boolean> = _isPausedBtwnRep

    private val _preActiveCount = MutableStateFlow(0)

    val videoUrl = MutableStateFlow<Uri?>(null)
    val thumbnailUrl = MutableStateFlow<Uri?>(null)

    private val _timeInCurrentRep = MutableStateFlow(0f)
    val timeInCurrentRep: StateFlow<Float> = _timeInCurrentRep

    var exerciseDuration = 0
    private var durationJob: Job? = null
    private var countdownJob: Job? = null
    private var timerJob: Job? = null
    private val preActiveSteps = 6
    private val pausePerRepMillis = 1500L

    val _provider =  MutableStateFlow<Provider?>(null)
    val provider: StateFlow<Provider?> = _provider

    val repCount: StateFlow<Int> = combine(
        counter,
        exerciseState
    ) { rawCounter, state ->
        if (state == ExerciseState.active) {
            maxOf(1, rawCounter.toInt() + 1)
        } else {
            0
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val elapsedSeconds: StateFlow<Int> = timerValue

    fun timePerRepMillis(exercise: ExerciseEntry): Long {
        val totalTime = exercise.time
        val reps = exercise.reps
        return ((totalTime.toFloat() / reps) * 1000).toLong()
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
        if(workout.isWorkoutComplete())return
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
            exerciseDuration = 0
            startExerciseDurationTimer()
            startState(ExerciseState.setsStart)
        } else {
            startState(ExerciseState.setsFinished)
        }
        updateWorkout()
    }

    fun toNextSet() {
        val workout = _currentWorkout.value ?: return
        if(workout.isWorkoutComplete())return
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
            exerciseDuration = 0
            startExerciseDurationTimer()
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
                    while (!_isPaused.value && _timerValue.value < totalTimeSec) {
                        delay(1000)
                        _timerValue.value += 1
                    }
                    if (!_isPaused.value) {
                        startState(ExerciseState.preRest)
                    }
                }

                "Reps" -> {
                    val reps = exercise.reps
                    val timePerRep = timePerRepMillis(exercise)

                    var currentRep = _counter.value.toInt() + 1

                    while (currentRep <= reps && !_isPaused.value) {
                        delay(timePerRep)
                        if (_isPaused.value) break

                        _isPausedBtwnRep.value = true
                        delay(pausePerRepMillis)
                        _isPausedBtwnRep.value = false
                        if (_isPaused.value) break

                        _counter.value = currentRep.toFloat()
                        currentRep++
                    }

                    if (!_isPaused.value && currentRep > reps) {
                        startState(ExerciseState.preRest)
                    }
                }

                else -> {
                    val totalTimeSec = 30
                    while (!_isPaused.value && _timerValue.value < totalTimeSec) {
                        delay(1000)
                        _timerValue.value += 1
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
            while (_timerValue.value < restSeconds) {
                while (_isPaused.value) delay(100)
                delay(1000)
                _timerValue.value += 1
            }
            startState(ExerciseState.preActive)
        }
    }

    fun loadWorkout(workoutId: String) {
        val workout = memberRepository.getSelectedWorkout(workoutId)
        Log.e("workout", "${workout}")
        _currentWorkout.value = workout

        _provider.value = workout?._program?.providerId?.let { memberRepository.getProvider(it) }

        val inProgress = workout?.exerciseEntryInProgress
        _currentExerciseEntry.value = inProgress?.currentEntry
        _currentSet.value = inProgress?.currentSet ?: 1
        _isPaused.value = inProgress?.isPaused ?: false
        _isPausedBtwnRep.value = inProgress?.isPausedBtwnRep ?: false

        val frameCount = inProgress?.counter ?: 0
        val totalSeconds = frameCount / 30f
        _timerValue.value = totalSeconds.toInt()

        val reps = _currentExerciseEntry.value?.reps ?: 1
        val timePerRep = _currentExerciseEntry.value?.timePerRep()?.toFloat() ?: 1f

        _counter.value = min((totalSeconds / timePerRep), reps.toFloat())
        val timeIntoCurrentRep = totalSeconds % timePerRep
        _timeInCurrentRep.value = timeIntoCurrentRep

        _exerciseState.value = inProgress?.exerciseState ?: ExerciseState.setsStart
        exerciseDuration = inProgress?.counter ?: 0

        if (inProgress?.exerciseState != null && inProgress.isPaused == false) {
            startState(inProgress.exerciseState)
            if (inProgress.exerciseState == ExerciseState.active) {
                startExerciseDurationTimer()
            }
        } else {
            if (inProgress?.isPaused == true)
                _stateText.value = ""
            else
                updateStateText()
        }
    }

    private fun startExerciseDurationTimer() {
        durationJob?.cancel()
        durationJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L / 30)
                if (!_isPaused.value && _exerciseState.value == ExerciseState.active) {
                    exerciseDuration++
                }
            }
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
        _timerValue.value = 0
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
            exerciseDuration = 0
            startExerciseDurationTimer()
        } else {
            startState(ExerciseState.setsFinished)
            saveFinishedSet()
            updateWorkout()
            return
        }
        val restSeconds = _currentExerciseEntry.value?.rest ?: 3
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerValue.value < restSeconds) {
                while (_isPaused.value) delay(100)
                delay(1000)
                _timerValue.value += 1
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
        _timerValue.value = 0
        _counter.value = 0f

        val exercise = _currentExerciseEntry.value ?: return
        val basedType = exercise.basedType

        timerJob = viewModelScope.launch {
            when (basedType) {
                "Time" -> {
                    val totalTime = exercise.time
                    while (_timerValue.value < totalTime) {
                        while (_isPaused.value) delay(100)
                        delay(1000)
                        _timerValue.value += 1
                    }
                    startState(ExerciseState.preRest)
                }

                "Reps" -> {
                    val reps = exercise.reps
                    val timePerRep = timePerRepMillis(exercise)

                    for (rep in 1..reps) {
                        while (_isPaused.value) delay(100)
                        delay(timePerRep)
                        while (_isPaused.value) delay(100)
                        _isPausedBtwnRep.value = true
                        delay(pausePerRepMillis)
                        _isPausedBtwnRep.value = false
                        _counter.value = rep.toFloat()
                    }
                    startState(ExerciseState.preRest)
                }

                else -> {
                    val fallbackTime = 30
                    while (_timerValue.value < fallbackTime) {
                        while (_isPaused.value) delay(100)
                        delay(1000)
                        _timerValue.value += 1
                    }
                    startState(ExerciseState.preRest)
                }
            }
        }
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
        viewModelScope.launch {
            val updatedEntryInProgress = ExerciseEntryInProgress(
                currentEntry = currentExerciseEntry.value,
                exerciseState = exerciseState.value,
                currentSet = currentSet.value,
                isPaused = isPaused.value,
                isPausedBtwnRep = isPausedBtwnRep.value,
                counter = exerciseDuration
            )
            currentWorkout.value?.let { memberRepository.updateWorkout(it, updatedEntryInProgress) }
        }
    }
}