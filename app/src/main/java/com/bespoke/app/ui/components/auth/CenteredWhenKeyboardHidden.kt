package com.bespoke.app.ui.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CenteredWhenKeyboardHidden(
    modifier: Modifier = Modifier,
    imeVisible: Boolean,
    content: @Composable ColumnScope.() -> Unit

) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(
                if (!imeVisible) Modifier.padding(top = 164.dp) else Modifier.padding(top = 32.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}
