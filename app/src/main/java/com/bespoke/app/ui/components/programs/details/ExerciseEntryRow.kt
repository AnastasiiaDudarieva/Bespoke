package com.bespoke.app.ui.components.programs.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bespoke.app.data.model.ExerciseEntry

@Composable
fun ExerciseEntryRow(
    entry: ExerciseEntry,
    state: ExerciseState = ExerciseState.NotStarted,
    imageUrl: String? = null // Якщо ти хочеш передавати URL ззовні (як після loadExerciseData)
) {
    var url by remember { mutableStateOf(imageUrl) }

    LaunchedEffect(entry) {
        if (url == null) {
            val square = entry.exerciseMedia?.firstOrNull()?.squarePath
            if (square != null) {
                // Тут припускаємо, що є метод який повертає url з gs://
                url = resolveFirebaseUrl(square)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .size(112.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = entry.name ?: "No name",
                fontSize = 16.sp,
                color = Color.Black
            )

//            Text(
////                text = entry.parameterListString ?: "",
//                fontSize = 14.sp,
//                color = Color.Gray
//            )

            when (state) {
                ExerciseState.Complete -> StatusLabel("Completed", Color(0xFF9E9E9E))
                ExerciseState.InProgress -> StatusLabel("In Progress", Color(0xFF6A1B9A))
                else -> {}
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                entry.mediaList?.forEach { item ->
                    val icon = when (item.kind?.lowercase()) {
                        "video" -> Icons.Default.PlayCircle
                        "audio" -> Icons.Default.Audiotrack
                        else -> null
                    }

                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(15.dp)
                        )
                    }
                }

                if (!entry.comments.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatusLabel(text: String, dotColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 4.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(dotColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp)
    }
}

enum class ExerciseState {
    NotStarted, InProgress, Complete, preActive
}

// Заглушка — заміни своїм кодом
suspend fun resolveFirebaseUrl(path: String): String {
    // TODO: Отримай URL з FirebaseStorage, наприклад:
    // Firebase.storage.getReference(path).downloadUrl.await().toString()
    return "https://via.placeholder.com/112"
}
