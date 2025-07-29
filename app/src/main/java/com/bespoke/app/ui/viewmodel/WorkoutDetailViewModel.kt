package com.bespoke.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.data.model.Workout
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

    // --- Workout / Exercise state ---
    private val _currentWorkout = MutableStateFlow<Workout?>(null)

    private val _currentExerciseEntry = MutableStateFlow<ExerciseEntry?>(null)
    val currentExerciseEntry: StateFlow<ExerciseEntry?> = _currentExerciseEntry

    private val _exerciseState = MutableStateFlow(ExerciseState.setsFinished)
    val exerciseState: StateFlow<ExerciseState> = _exerciseState

    private val _currentSet = MutableStateFlow(1)
    val currentSet: StateFlow<Int> = _currentSet

    private val _isPaused = MutableStateFlow(true)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _timerValue = MutableStateFlow(0)
    private val timerValue: StateFlow<Int> = _timerValue

    private val _counter = MutableStateFlow(0f)
    private val counter: StateFlow<Float> = _counter

    private val _stateText = MutableStateFlow("")
    val stateText: StateFlow<String> = _stateText

    private val _preActiveCount = MutableStateFlow(0)

    val videoUrl = MutableStateFlow<Uri?>(null)
    val thumbnailUrl = MutableStateFlow<Uri?>(null)


    private var countdownJob: Job? = null
    private var timerJob: Job? = null
    private val preActiveSteps = 6
    private val pausePerRepMillis = 1500L


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

        if (currentState == ExerciseState.setsFinished) {
            _isPaused.value = false
            startState(ExerciseState.setsStart)
            return
        }

        val wasPaused = _isPaused.value
        _isPaused.value = !wasPaused

        if (_isPaused.value) {
            countdownJob?.cancel()
            timerJob?.cancel()
            _stateText.value = "Paused"
        } else {
            _stateText.value = ""
            when (currentState) {
                ExerciseState.preActive -> startPreActiveCountDown()
                ExerciseState.active -> resumeActive()
                ExerciseState.rest -> resumeRest()
                else -> updateStateText()
            }
        }
    }

    fun toNextExercise() {
        val workout = _currentWorkout.value ?: return
        val allExercises = workout._program?.sections?.flatMap { it.entries ?: emptyList() } ?: return
        val current = _currentExerciseEntry.value ?: return
        val currentIndex = allExercises.indexOfFirst { it.id == current.id }

        _isPaused.value = true
        timerJob?.cancel()
        countdownJob?.cancel()

        if (currentIndex + 1 < allExercises.size) {
            _currentExerciseEntry.value = allExercises[currentIndex + 1]
            _currentSet.value = 1
            loadVideoUrlIfNeeded()
            startState(ExerciseState.setsStart)
        } else {
            startState(ExerciseState.setsFinished)
        }
    }

    fun toNextSet() {
        val workout = _currentWorkout.value ?: return
        val allExercises = workout._program?.sections?.flatMap { it.entries ?: emptyList() } ?: return
        val current = _currentExerciseEntry.value ?: return
        val currentIndex = allExercises.indexOfFirst { it.id == current.id }

        _isPaused.value = true
        timerJob?.cancel()
        countdownJob?.cancel()

        if (_currentSet.value < current.sets) {
            _currentSet.value += 1
            startState(ExerciseState.setsStart)
        } else if (currentIndex + 1 < allExercises.size) {
            _currentExerciseEntry.value = allExercises[currentIndex + 1]
            _currentSet.value = 1
            loadVideoUrlIfNeeded()
            startState(ExerciseState.setsStart)
        } else {
            startState(ExerciseState.setsFinished)
        }
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

                        delay(pausePerRepMillis)
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
        _currentWorkout.value = workout
        val allExercises =
            workout?._program?.sections?.flatMap { it.entries ?: emptyList() } ?: emptyList()
        _currentExerciseEntry.value = allExercises.firstOrNull()
        _currentSet.value = 1
        loadVideoUrlIfNeeded()
    }

    private fun startState(state: ExerciseState) {
        _exerciseState.value = state
        updateStateText()

        when (state) {
            ExerciseState.setsStart -> viewModelScope.launch {
                delay(1000)
                startState(ExerciseState.preActive)
            }

            ExerciseState.preActive -> startPreActiveCountDown()

            ExerciseState.active -> startActiveTimer()

            ExerciseState.preRest -> viewModelScope.launch {
                delay(1000)
                startState(ExerciseState.rest)
            }

            ExerciseState.rest -> {
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
                    loadVideoUrlIfNeeded()
                } else {
                    startState(ExerciseState.setsFinished)
                    return
                }

                val restSeconds = _currentExerciseEntry.value?.rest ?: 30

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


            ExerciseState.setsFinished -> _stateText.value = "Workout Complete"
        }
    }

    private fun proceedToNextSetOrFinish() {
        val totalSets = currentExerciseEntry.value?.sets ?: 3
        if (_currentSet.value < totalSets) {
            _currentSet.value += 1
            startState(ExerciseState.preActive)
        } else {
            startState(ExerciseState.setsFinished)
        }
    }

    private fun updateStateText() {
        if (_isPaused.value) {
            _stateText.value = "Paused"
            return
        }

        _stateText.value = when (_exerciseState.value) {
            ExerciseState.setsStart -> ""
            ExerciseState.rest -> "Recover"
            ExerciseState.setsFinished -> "Workout Complete"
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
        val basedType = exercise.basedType ?: "Reps"

        timerJob = viewModelScope.launch {
            when (basedType) {
                "Time" -> {
                    val totalTime = exercise.time ?: 30
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
                        delay(pausePerRepMillis)
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

    fun loadVideoUrlIfNeeded() {
        val media =
            currentExerciseEntry.value?.exerciseMedia?.firstOrNull { it.kind == "video" } ?: return
        val videoPath = media.path ?: return
        val thumbPath = media.thumbnailPath ?: return

        viewModelScope.launch {
            val thumbCached = FirebaseStorageUrlCache.get(thumbPath)
            thumbnailUrl.value = thumbCached ?: getFirebaseDownloadUrl(thumbPath)?.also {
                FirebaseStorageUrlCache.set(thumbPath, it)
            }

            val videoCached = FirebaseStorageUrlCache.get(videoPath)
            videoUrl.value = videoCached ?: getFirebaseDownloadUrl(videoPath)?.also {
                FirebaseStorageUrlCache.set(videoPath, it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        countdownJob?.cancel()
    }

    fun selectExercise(currentExercise: ExerciseEntry) = memberRepository.selectExercise(currentExercise)

}
