package com.bespoke.app.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.util.Calendar
import java.util.Date

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


fun Date.toStartOfDay(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}
fun Calendar.getDayName(): String {
    return when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY    -> "sun"
        Calendar.MONDAY    -> "mon"
        Calendar.TUESDAY   -> "tue"
        Calendar.WEDNESDAY -> "wed"
        Calendar.THURSDAY  -> "thu"
        Calendar.FRIDAY    -> "fri"
        Calendar.SATURDAY  -> "sat"
        else               -> "unknown"
    }
}

