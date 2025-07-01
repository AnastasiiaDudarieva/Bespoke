package com.bespoke.app.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@IgnoreExtraProperties
data class Member(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    var createdAt: Int? = null,
    var dob: Int? = null,
) {
    val fullName: String get() = "$firstName\n$lastName"

}

fun Member.dobFormatted(): String {
    val safeDob = dob ?: return ""
    if (safeDob <= 0) return ""

    val date = Date(safeDob * 1000L)
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    return formatter.format(date)
}