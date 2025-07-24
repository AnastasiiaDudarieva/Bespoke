package com.bespoke.app.ui.components.programs.details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.R
import com.bespoke.app.data.model.Program
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.viewmodel.ProgramOverviewViewModel

@Composable
fun EquipmentNeeded(program: Program, viewModel: ProgramOverviewViewModel) {

    val equipmentLabels = remember(program) {
        program.sections.flatMap { it.entries ?: emptyList() }
            .flatMap { it.equipmentIds.orEmpty() }
            .mapNotNull { id -> viewModel.getEquipmentLabelById(id) }
            .toSet()
            .sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBackgroundColor)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.equipment_needed), fontFamily = BeatriceFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            color = TextDark
        )
        Spacer(Modifier.padding(8.dp))
        Text(
            text = equipmentLabels.joinToString("  •  "),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
