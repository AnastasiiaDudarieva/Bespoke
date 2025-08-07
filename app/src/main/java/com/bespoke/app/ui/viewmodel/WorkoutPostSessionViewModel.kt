package com.bespoke.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bespoke.app.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutPostSessionViewModel @Inject constructor(
    memberRepository: MemberRepository
) : ViewModel() {
    val selectedWorkout = memberRepository.selectedWorkout
}
