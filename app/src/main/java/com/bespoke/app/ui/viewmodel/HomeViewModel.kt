package com.bespoke.app.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.ui.components.base.imageBitmapCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    val member = memberRepository.member

    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar = _isUploadingAvatar.asStateFlow()

    fun uploadProfileImage(image: Bitmap) {
        viewModelScope.launch {
            try {
                _isUploadingAvatar.value = true
                val newAvatarUrl = memberRepository.uploadProfileImage(image)
                if (newAvatarUrl != null) {
                    imageBitmapCache[newAvatarUrl] = image
                    memberRepository.updateMemberAvatar(newAvatarUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isUploadingAvatar.value = false
            }
        }
    }


    fun logout() {
        memberRepository.logout()
    }

}