package com.bespoke.app.ui.screens.components.programs.details

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bespoke.app.ui.theme.BorderGrayColor
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerView(url: String) {
    LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(1) }

    val mediaPlayer = remember {
        MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                duration = it.duration
            }
            setOnCompletionListener {
                isPlaying = false
                currentPosition = duration
            }
            prepareAsync()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition
            delay(500L)
        }
    }

    fun formatMillis(millis: Int): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    color = BorderGrayColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Voice Note",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Play Button
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                mediaPlayer.pause()
                            } else {
                                if (currentPosition >= duration) mediaPlayer.seekTo(0)
                                mediaPlayer.start()
                            }
                            isPlaying = !isPlaying
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White
                        )
                    }

                    LinearProgressIndicator(
                        progress = currentPosition / duration.toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .padding(horizontal = 8.dp)
                    )

                    // Replay Button
                    IconButton(
                        onClick = {
                            mediaPlayer.seekTo(0)
                            if (!isPlaying) {
                                mediaPlayer.start()
                                isPlaying = true
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Replay",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Time labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatMillis(currentPosition))
                    Text(text = "-${formatMillis(duration - currentPosition)}")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

