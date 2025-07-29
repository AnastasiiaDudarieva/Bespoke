package com.bespoke.app.ui.screens.components.programs.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ProgramSection
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.viewmodel.ProgramOverviewViewModel
import java.util.Locale

@Composable
fun ProgramSectionBlock(
    section: ProgramSection,
    index: Int,
    viewModel: ProgramOverviewViewModel,
    onExerciseClick: (ExerciseEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                section.title ?: "",
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f),
                fontFamily = BeatriceFontFamily,
                fontWeight = FontWeight.W600
            )
            Text(
                String.format(Locale.ENGLISH,"%02d", index),
                fontSize = 14.sp,
                fontFamily = BeatriceFontFamily,
                fontWeight = FontWeight.W600
            )
        }

        section.entries?.forEach { entry ->
            ExerciseEntryRow(entry = entry, viewModel = viewModel,
                onExerciseClick = onExerciseClick)
        }
        Spacer(Modifier.height(48.dp))

    }
}
