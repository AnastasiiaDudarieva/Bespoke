package com.bespoke.app.ui.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.repository.MemberRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class DisplayedMedia(
    val url: String,
    val type: MediaType,
)

enum class MediaType {
    IMAGE, AUDIO
}

@HiltViewModel
class MediaManagerViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _exercise = MutableStateFlow<ExerciseEntry?>(null)
    val exercise: StateFlow<ExerciseEntry?> = _exercise

    private val _displayedMedia = MutableStateFlow<List<DisplayedMedia>>(emptyList())
    val displayedMedia: StateFlow<List<DisplayedMedia>> = _displayedMedia

    private val _voiceRecordingUrl = MutableStateFlow<String?>(null)
    val voiceRecordingUrl: StateFlow<String?> = _voiceRecordingUrl

    private var audioPlayer: MediaPlayer? = null

    private val _isMediaNotEmpty = MutableStateFlow(false)
    val isMediaNotEmpty: StateFlow<Boolean> = _isMediaNotEmpty


    fun loadExerciseData(id: String) {
        viewModelScope.launch {
            val exercise = memberRepository.getSelectedExercise()
            if (exercise?.id == id) {
                _exercise.value = exercise
                loadMediaFromEntry(exercise)
            }
        }
    }

    private suspend fun loadMediaFromEntry(entry: ExerciseEntry) {
        val mediaItems = mutableListOf<DisplayedMedia>()
        entry.mediaList?.filter { it.kind == "image" }?.forEach { media ->
            media.path?.let {
                val url = resolveFirebaseUrl(it)
                mediaItems.add(DisplayedMedia(url = url, type = MediaType.IMAGE))
            }
        }
        entry.mediaList?.firstOrNull { it.kind == "audio" }?.let { media ->
            media.path?.let {
                val url = resolveFirebaseUrl(it)
                Log.e("MediaManagerViewModel", "Audio URL: $url")
                _voiceRecordingUrl.value = url
            }
        }

        _displayedMedia.value = mediaItems
        _isMediaNotEmpty.value = _displayedMedia.value.isNotEmpty() || _voiceRecordingUrl.value != null
    }

    suspend fun resolveFirebaseUrl(path: String): String {
        return Firebase.storage.getReference(path).downloadUrl.await().toString()
    }

    fun playVoiceRecording() {
        stopPlayback()

        _voiceRecordingUrl.value?.let { url ->
            audioPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepare()
                start()
            }
        }
    }

    fun stopPlayback() {
        audioPlayer?.release()
        audioPlayer = null
    }

    fun parameterListString(entry: ExerciseEntry): String {
        entry.apply {
            var label = ""
            when (basedType) {
                "Reps" -> label = "$sets Sets  •  $reps Reps"
                "Time" -> label = "$time Sec"
                "Time & Reps" -> label = "$time Sec  •  $sets Sets  •  $reps Reps"
            }

            val hasWeights =
                equipmentIds?.any { it in com.bespoke.app.data.model.Equipment.weightEquipmentIds }
            if (hasWeights == true) {
                label += "  •  $weight lbs"
            }

            return label
        }
    }

    override fun onCleared() {
        stopPlayback()
        super.onCleared()
    }
}
