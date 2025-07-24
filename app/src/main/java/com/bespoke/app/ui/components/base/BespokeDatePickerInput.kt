package com.bespoke.app.ui.components.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.TextDark

@Composable
fun BespokeDatePickerInput(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = label,
                    fontFamily = BeatriceFontFamily,
                    fontSize = 16.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = BeatriceFontFamily,
                fontSize = 16.sp
            ),
            shape = RoundedCornerShape(4.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = InputBackgroundColor,
                disabledContainerColor = Color.Transparent,
                disabledLabelColor = TextDark,
                disabledTextColor = TextDark,
                cursorColor = Color.Transparent
            )
        )
    }
}



