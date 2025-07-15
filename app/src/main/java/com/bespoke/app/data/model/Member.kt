package com.bespoke.app.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@IgnoreExtraProperties
data class Member(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val location: String? = null,
    val email: String? = null,
    val gender: String? = null,
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
    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.format(date)
}