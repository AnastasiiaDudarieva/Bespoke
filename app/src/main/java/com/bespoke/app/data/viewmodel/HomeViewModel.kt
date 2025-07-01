package com.bespoke.app.data.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    val member = memberRepository.member

    fun uploadProfileImage(image: Bitmap) {
        viewModelScope.launch {
            memberRepository.uploadProfileImage(image)
        }
    }
    fun logout() {
        memberRepository.logout()
    }

}