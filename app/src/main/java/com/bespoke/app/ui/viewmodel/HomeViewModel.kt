package com.bespoke.app.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bespoke.app.data.model.StreakDataStats
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.ui.components.base.imageBitmapCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

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

    fun getLongestStreakDisplay(): String {
        val longest = memberRepository.getStreakDataStats()?.longestStreak ?: 0
        return if (longest == 1) "1 Day" else "$longest Days"
    }

    fun getCompletedProgramCount(): String {
        val completedPrograms = memberRepository.pastWorkouts.value.count { it.didComplete }
        return "$completedPrograms Complete"
    }

    fun getTotalBurnedCalories(): String{
        val totalCalories = memberRepository.pastWorkouts.value.sumOf { it.calloriesBurned }.roundToInt()
        return "$totalCalories Burned"
    }

    fun logout() {
        memberRepository.logout()
    }

}