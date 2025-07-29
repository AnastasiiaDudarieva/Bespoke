package com.bespoke.app.ui.screens.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bespoke.app.ui.models.profile.ProfileStatsType
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark

@Composable
fun StatsCard(
    type: ProfileStatsType,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(type.backgroundColor)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = type.iconRes),
                contentDescription = stringResource(id= type.titleRes),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id= type.titleRes),
                fontFamily = BeatriceFontFamily,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            fontFamily = BeatriceFontFamily,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark
        )
    }
}
