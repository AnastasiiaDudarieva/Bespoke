package com.bespoke.app.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bespoke.app.data.model.Member
import com.bespoke.app.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    val member: StateFlow<Member?> = memberRepository.member

}
