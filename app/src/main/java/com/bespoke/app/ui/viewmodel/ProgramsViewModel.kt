package com.bespoke.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.PastWorkout
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.model.UpcomingProgram
import com.bespoke.app.data.model.Workout
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.utils.getDayName
import com.bespoke.app.utils.toStartOfDay
import com.bespoke.app.utils.zone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    var hasScrolledInitially = false

    private val _uiState = MutableStateFlow(ProgramsUiState())
    val uiState: StateFlow<ProgramsUiState> = _uiState

    init {
        viewModelScope.launch {
            launch {
                memberRepository.workouts.collect { updateState() }
            }
            launch {
                memberRepository.programs.collect { updateState() }
            }
        }
    }

    private fun updateState() {
        val publishedPrograms = memberRepository.programs.value
            .filter { it.status == Program.Status.PUBLISHED.value }

        val allCompleted = memberRepository.completedWorkouts

        val completedWorkoutsToday = allCompleted.filter { w ->
            val ts = w.completedAt ?: return@filter false
            isDateInTodaySec(ts.toLong())
        }

        val inProgressWorkouts = memberRepository.workouts.value.filter { it.completedAt == null }

        val todaysWorkouts = completedWorkoutsToday + inProgressWorkouts

        val todaysPrograms = publishedPrograms.filter { program ->
            program.days?.contains(todayWeekday()) == true &&
                    inProgressWorkouts.none { it.programId == program.id } &&
                    completedWorkoutsToday.none { it.programId == program.id }
        }

        val upcomingPrograms = (1..6).flatMap { offset ->
            val day = weekdayFromToday(offset)
            publishedPrograms.filter { it.days?.contains(day) == true }.map {
                UpcomingProgram(day = day, offset = offset, program = it)
            }
        }

        val pastWorkouts = memberRepository.pastWorkoutsWithoutToday

        _uiState.value = ProgramsUiState(
            pastWorkouts = pastWorkouts,
            todayWorkouts = todaysWorkouts,
            todayPrograms = todaysPrograms,
            upcomingPrograms = upcomingPrograms
        )
    }

    private fun isDateInTodaySec(epochSec: Long): Boolean =
        Instant.ofEpochSecond(epochSec).atZone(zone).toLocalDate() == LocalDate.now(zone)

    suspend fun loadProgramData(program: Program) = memberRepository.loadExerciseData(program)

    fun loadWorkoutData(workout: Workout) =
        viewModelScope.launch { memberRepository.loadWorkoutData(workout) }

    private fun todayWeekday(): String = weekdayFromToday(0)

    private fun weekdayFromToday(offset: Int): String {
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
        return calendar.getDayName()
    }
}

data class ProgramsUiState(
    val pastWorkouts: List<PastWorkout> = emptyList(),
    val todayWorkouts: List<Workout> = emptyList(),
    val todayPrograms: List<Program> = emptyList(),
    val upcomingPrograms: List<UpcomingProgram> = emptyList(),
)
