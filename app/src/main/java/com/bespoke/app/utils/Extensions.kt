package com.bespoke.app.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    composed {
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    }

fun String.formatPhoneNumber(mask: String = "+XXXX-XXX-XXXX"): String {
    val clean = this.filter { it.isDigit() }

    val result = StringBuilder()
    var digitIndex = 0

    for (char in mask) {
        if (digitIndex >= clean.length) break
        if (char == 'X') {
            result.append(clean[digitIndex])
            digitIndex++
        } else {
            result.append(char)
        }
    }

    return result.toString().replace("++", "+")
}