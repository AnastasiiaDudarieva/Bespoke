package com.bespoke.app.data.model

import java.util.Calendar
import java.util.Date

data class Program(
    val id: String = "",
    val title: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val days: List<Int> = emptyList(), // 1 = Sunday, 2 = Monday...
    val focus: String = "",
    val sections: List<ProgramSection> = emptyList(),
    val status: String = "draft",
    val providerId: String = "",
    val memberIds: List<String> = emptyList(),
    val version: Int = 1,
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
        var date = Date(createdAt * 1000)

        val cal = Calendar.getInstance()
        while (date.before(Date())) {
            cal.time = date
            if (days.contains(cal.get(Calendar.DAY_OF_WEEK))) {
                result.add(cal.time)
            }
            cal.add(Calendar.DATE, 1)
            date = cal.time
        }
        return result
    }
}
//
//fun Program.requiredWorkoutDays(): List<Date> {
//    val startDate = Date(createdAt * 1000L)
//    val result = mutableListOf<Date>()
//    var current = startDate
//
//    val workoutDays = days.mapNotNull { it.dayOfWeekId }
//
//    while (current.before(Date())) {
//        val cal = Calendar.getInstance().apply { time = current }
//        if (workoutDays.contains(cal.get(Calendar.DAY_OF_WEEK))) {
//            result.add(cal.time)
//        }
//        cal.add(Calendar.DAY_OF_MONTH, 1)
//        current = cal.time
//    }
//
//    return result
//}
