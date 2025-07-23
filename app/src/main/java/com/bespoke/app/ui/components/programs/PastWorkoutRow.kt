package com.bespoke.app.ui.components.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.bespoke.app.data.model.PastWorkout
import com.bespoke.app.data.model.lengthDisplay
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BorderGrayColor
import com.bespoke.app.ui.theme.Green
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.Orange
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PastWorkoutRow(
    pastWorkout: PastWorkout,
    modifier: Modifier = Modifier,
    onClick:()-> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 0.5.dp,
                color = BorderGrayColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
              onClick()
            }
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dayString(pastWorkout.completedAt),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextDark.copy(alpha = 0.5f),
                        letterSpacing = 0.08.em
                    ),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                val iconRes =
                    if (pastWorkout.didComplete) R.drawable.ic_check else R.drawable.ic_close
                val iconTint = if (pastWorkout.didComplete) TextDark else White
                val circleColor = if (pastWorkout.didComplete) Green else Orange

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(circleColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = pastWorkout.program.title ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextDark.copy(alpha = 0.5f)
                ),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${pastWorkout.program.lengthDisplay()} MIN",
                    fontFamily = BeatriceFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = TextDark.copy(alpha = 0.5f)
                )

//                if (isLoading) {
//                    Spacer(modifier = Modifier.width(8.dp))
//                    CircularProgressIndicator(
//                        strokeWidth = 2.dp,
//                        modifier = Modifier.size(16.dp)
//                    )
//                }

                Spacer(modifier = Modifier.weight(1f))
            }

            WorkoutStatusView(
                title = if (pastWorkout.didComplete) "Completed" else "Missed",
                color = if (pastWorkout.didComplete) Green else Orange
            )
        }
    }
}

@Composable
fun WorkoutStatusView(title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(InputBackgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp)
            .height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(color = TextDark)
        )
    }
}

fun dayString(timestamp: Int?): String {
    if (timestamp == null) return ""
    val date = Date(timestamp * 1000L)
    val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return formatter.format(date)
}
