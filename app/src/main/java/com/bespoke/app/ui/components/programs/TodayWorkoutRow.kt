package com.bespoke.app.ui.components.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bespoke.app.R
import com.bespoke.app.data.model.Workout
import com.bespoke.app.data.model.lengthDisplay
import com.bespoke.app.ui.components.base.FirebaseStorageImageView
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.TextDark


@Composable
fun TodayWorkoutRow(
    workout: Workout,
    onResumeClick: (Workout) -> Unit,
    modifier: Modifier = Modifier,
) {
    val program = workout._program
    val completedExercisesCount =
        workout.completedExerciseEntries?.values?.count { it.status == "setsFinished" }
    val totalExercises = program?.sections?.flatMap { it.entries!! }?.size
    val workoutIsComplete = completedExercisesCount == totalExercises && workout.effort != null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(344.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray)
    ) {
        // Background image
        if (program?.thumbnail?.isNotBlank() == true) {
            FirebaseStorageImageView(gsPath = program.thumbnail)
        } else {
//            Image(
//                painter = painterResource(id = R.drawable.background_primary),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.matchParentSize()
//            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "TODAY'S PROGRAM",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .padding(
                        top = 36.dp,
                        start = 24.dp, end = 40.dp
                    ),

                letterSpacing = 0.08.em,
                fontFamily = BeatriceFontFamily,
                fontWeight = FontWeight.W600,
                color = Color.White
            )

            Text(
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = 24.dp, end = 40.dp
                    ),
                lineHeight = 48.sp,
                text = program?.title ?: "",
                fontSize = 40.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontFamily = BeatriceFontFamily
            )

            Text(
                text = "${program?.lengthDisplay()} MIN",
                fontFamily = BeatriceFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 24.dp, end = 24.dp
                ),
            )

            if (workoutIsComplete) {
                StatusLabel("Completed", Color(0xFF59C576))
            } else {
                StatusLabel("In Progress", BespokeBlue)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!workoutIsComplete) {
                Row(
//                        onClick = { onResumeClick(workout) },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(BespokeBlue)
                        .padding(vertical = 24.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resume Program",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = BeatriceFontFamily
                    )
                    Icon(
                        modifier = Modifier
                            .size(50.dp),
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun StatusLabel(text: String, dotColor: Color) {
    Box(
        modifier = Modifier
            .padding(start = 24.dp, top = 14.dp, end = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp)
                .height(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = TextDark,
                fontFamily = BeatriceFontFamily
            )
        }
    }
}

