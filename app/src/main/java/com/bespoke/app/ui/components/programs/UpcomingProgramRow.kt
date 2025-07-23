package com.bespoke.app.ui.components.programs

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bespoke.app.data.model.Program
import com.bespoke.app.data.model.UpcomingProgram
import com.bespoke.app.data.model.lengthDisplay
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BorderGrayColor
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.viewmodel.ProgramsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun UpcomingProgramRow(
    modifier: Modifier = Modifier,
    upcomingProgram: UpcomingProgram,
    onClick: () -> Unit,
    viewModel: ProgramsViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 0.5.dp,
                color = BorderGrayColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !isLoading) {
                coroutineScope.launch {
                    isLoading = true
                    viewModel.loadProgramData(program = upcomingProgram.program)
                    onClick()
                    isLoading = false
                }
            }
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = dayStringFromToday(offset = upcomingProgram.offset),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextDark,
                    letterSpacing = 0.08.em
                ),
                lineHeight = 16.sp
            )

            Text(
                text = upcomingProgram.program.title ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextDark
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
                    text = "${upcomingProgram.program.lengthDisplay()} MIN",
                    fontFamily = BeatriceFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = TextDark
                )

                if (isLoading) {
                    Spacer(modifier = Modifier.width(12.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = TextDark.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

fun dayStringFromToday(offset: Int): String {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, offset)
    }
    val format = SimpleDateFormat("EEEE", Locale.getDefault()) // e.g., "Monday"
    return format.format(calendar.time)
}