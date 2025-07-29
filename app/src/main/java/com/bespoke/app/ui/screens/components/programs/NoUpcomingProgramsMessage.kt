package com.bespoke.app.ui.screens.components.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bespoke.app.R
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.TextDark

@Composable
fun NoUpcomingProgramsMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(24.dp)
    ) {
        Text(text = stringResource(R.string.upcoming_programs_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextDark)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.upcoming_programs_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark
        )
    }
}
