package com.bespoke.app.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bespoke.app.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    val member = memberRepository.member

    fun logout() {
        memberRepository.logout()
    }

}