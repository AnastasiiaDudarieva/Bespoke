package com.bespoke.app.ui.components.programs.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ProgramSection

@Composable
fun ProgramSectionBlock(
    section: ProgramSection,
    index: Int,
    onSelect: (ExerciseEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 64.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(section.title?:"", fontWeight = FontWeight.Bold)
            Text(String.format("%02d", index))
        }

        section.entries?.forEach { entry ->
            ExerciseEntryRow(entry = entry)
        }
    }
}
