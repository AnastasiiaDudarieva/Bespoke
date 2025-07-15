package com.bespoke.app.ui.components.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.theme.White

@Composable
fun BespokeInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val (actualTransformation, trailingIcon) = rememberVisualTransformationWithIcon(
        visualTransformation
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(
            fontFamily = BeatriceFontFamily,
            fontSize = 16.sp,
        ),
        label = {
            Text(
                text = label,
                fontFamily = BeatriceFontFamily,
                fontSize = 16.sp,
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = actualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = BespokeBlue,
            unfocusedIndicatorColor = InputBackgroundColor,
            focusedLabelColor = BespokeBlue,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = BespokeBlue,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        )
    )
}


@Composable
private fun rememberVisualTransformationWithIcon(
    original: VisualTransformation,
): Pair<VisualTransformation, (@Composable () -> Unit)?> {
    if (original !is PasswordVisualTransformation) {
        return original to null
    }

    var visible by rememberSaveable { mutableStateOf(false) }

    val icon = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff
    val description = if (visible) "Hide password" else "Show password"

    val trailingIcon: @Composable () -> Unit = {
        IconButton(onClick = { visible = !visible }) {
            Icon(imageVector = icon, contentDescription = description)
        }
    }

    val currentTransformation = if (visible) VisualTransformation.None else original

    return currentTransformation to trailingIcon
}
