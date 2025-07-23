package com.bespoke.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.Equipment
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.repository.MemberRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProgramOverviewViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    val programs = memberRepository.programs
    private val _program = MutableStateFlow<Program?>(null)
    val program: StateFlow<Program?> = _program

    fun getEquipmentLabelById(id: String) = memberRepository.getEquipmentLabelById(id)

    fun loadProgramData(program: Program) {
        viewModelScope.launch {
            val updatedProgram = memberRepository.loadExerciseData(program)
            _program.value = updatedProgram
        }
    }

    fun parameterListString(entry: ExerciseEntry): String {
        entry.apply {
            var label = ""
            when (basedType) {
                "Reps" -> label = "$sets Sets  •  $reps Reps"
                "Time" -> label = "$time Sec"
                "Time & Reps" -> label = "$time Sec  •  $sets Sets  •  $reps Reps"
            }

            val hasWeights = equipmentIds?.any { it in Equipment.weightEquipmentIds }
            if (hasWeights == true)
                label += "  •  $weight lbs"

            return label
        }

    }

    suspend fun resolveFirebaseUrl(path: String): String {
        return Firebase.storage.getReference(path).downloadUrl.await().toString()

    }
}
