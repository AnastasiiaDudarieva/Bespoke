package com.bespoke.app.data.model

import android.util.Log
import com.bespoke.app.utils.getDayName
import com.bespoke.app.utils.toStartOfDay
import com.google.firebase.firestore.DocumentId
import java.util.Calendar
import java.util.Date

data class Program(
    @DocumentId
    val id: String? = null,
    val title: String?= null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val days: List<String>? = null, // sun, mon...
    val focus: String? = null,
    val sections: List<ProgramSection>? = null,
    val status: String? = null,
    val providerId: String?= null,
    val memberIds: List<String>? = null,
    val version: Int? = null,
    val thumbnail: String? = null,
) {
    enum class Status(val value: String) {
        DRAFT("draft"),
        PUBLISHED("published"),
        ARCHIVED("archived");

        companion object {
            fun fromValue(value: String): Status {
                return entries.firstOrNull { it.value == value } ?: DRAFT
            }
        }
    }

    fun requiredWorkoutDays(): List<Date> {
        val result = mutableListOf<Date>()
        var date = Date(createdAt?.times(1000) ?: 0L).toStartOfDay()
        val cal = Calendar.getInstance()
        while (date.before(Date().toStartOfDay())) {
            cal.time = date
            if (days?.contains(cal.getDayName()) == true) {
                result.add(cal.time)
            }
            cal.add(Calendar.DATE, 1)
            date = cal.time
        }
        return result
    }
}
