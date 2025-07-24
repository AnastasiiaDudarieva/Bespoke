package com.bespoke.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.PastWorkout
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.model.UpcomingProgram
import com.bespoke.app.data.model.Workout
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.utils.getDayName
import com.bespoke.app.utils.toStartOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

        val allWorkouts = memberRepository.completedWorkouts

        val today = Date().toStartOfDay()
        val tomorrow = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_YEAR, 1)
        }.time

        val completedWorkoutsToday = allWorkouts.filter {
            Date(it.completedAt!! * 1000L).toStartOfDay() == today
        }

        val inProgressWorkouts = memberRepository.workouts.value.filter {
            it.completedAt == null || it.effort == null
        }

        val todaysWorkouts = completedWorkoutsToday + inProgressWorkouts

        val todaysPrograms = publishedPrograms.filter { program ->
            program.days?.contains(todayWeekday()) == true &&
                    !inProgressWorkouts.any { it.programId == program.id } &&
                    !completedWorkoutsToday.any { it.programId == program.id }
        }

        val upcomingPrograms = (1..6).flatMap { offset ->
            val day = weekdayFromToday(offset)
            publishedPrograms.filter { it.days?.contains(day) == true }.map {
                UpcomingProgram(day = day, offset = offset, program = it)
            }
        }

        val pastWorkouts = memberRepository.pastWorkouts.value.filter { workout ->
            val completedDate = Date(workout.completedAt * 1000L)
            completedDate.before(today) || completedDate.after(tomorrow)
        }
        _uiState.value = ProgramsUiState(
            pastWorkouts = pastWorkouts,
            todayWorkouts = todaysWorkouts.sortedBy { it._program?.createdAt ?: 0L },
            todayPrograms = todaysPrograms,
            upcomingPrograms = upcomingPrograms
        )
    }

    suspend fun loadProgramData(program: Program) = memberRepository.loadExerciseData(program)

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
