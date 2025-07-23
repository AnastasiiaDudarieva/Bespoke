package com.bespoke.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramOverviewViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    val programs = memberRepository.programs
    private val _program = MutableStateFlow<Program?>(null)
    val program: StateFlow<Program?> = _program

    fun loadProgramData(program: Program) {
        viewModelScope.launch {
            val updatedProgram = memberRepository.loadExerciseData(program)
            _program.value = updatedProgram
        }
    }
}
