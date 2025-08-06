package com.bespoke.app.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.ui.screens.components.base.imageBitmapCache
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
    private val _statistics = MutableStateFlow(Statistics("0 Days", "0 Complete", "0 Burned"))
    val statistics = _statistics.asStateFlow()

    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar = _isUploadingAvatar.asStateFlow()

    init {
        viewModelScope.launch {
            memberRepository.pastWorkouts.collect { pastWorkouts ->
                if (pastWorkouts.isNotEmpty()) {
                    val longest = memberRepository.getStreakDataStats()?.longestStreak ?: 0
                    val completedPrograms =
                        pastWorkouts.count { it.didComplete }
                    val totalCalories =
                        pastWorkouts.sumOf { it.calloriesBurned }
                            .roundToInt()

                    val statistics = Statistics(
                        if (longest == 1) "1 Day" else "$longest Days",
                        "$completedPrograms Complete",
                        "$totalCalories Burned"
                    )
                    _statistics.value = statistics
                } else {
                    _statistics.value = Statistics("0 Days", "0 Complete", "0 Burned")
                }
            }
        }


    }

    fun uploadProfileImage(image: Bitmap) {
        viewModelScope.launch {
            try {
                _isUploadingAvatar.value = true
                val newAvatarUrl = memberRepository.uploadProfileImage(image)
                if (newAvatarUrl != null) {
                    imageBitmapCache[newAvatarUrl] = image
                    memberRepository.updateMemberFields(mapOf("avatar" to newAvatarUrl))
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

data class Statistics(
    val longestStreak: String,
    val completedPrograms: String,
    val totalCaloriesBurned: String,
)