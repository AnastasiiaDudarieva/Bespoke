package com.bespoke.app.data.model

import com.bespoke.app.utils.getDayName
import com.bespoke.app.utils.toStartOfDay
import com.google.firebase.firestore.DocumentId
import java.util.Calendar
import java.util.Date

data class Program(
    @DocumentId
    val id: String? = null,
    val title: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val days: List<String>? = null, // sun, mon...
    val focus: String? = null,
    val sections: List<ProgramSection> = emptyList(),
    val status: String? = null,
    val providerId: String? = null,
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
}

data class ProgramSection(
    val id: String? = null,
    val title: String? = null,
    val entries: List<ExerciseEntry>? = null,
)

data class ExerciseEntry(
    val id: String? = null,
    val name: String? = null,
    val exerciseId: String? = null,
    val equipmentIds: List<String>? = null,
    val basedType: String = "Reps", // "Time & Reps", "Time", "Reps"
    val reps: Int = 5,
    val time: Int = 30,
    val weight: Int = 10,
    val sets: Int = 3,
    val rest: Int = 30,
    val comments: String? = null,
    val exerciseMedia: List<Media>? = null,
    val mediaList: List<Media>? = null,
)

data class UpcomingProgram(
    val id: String? = null,
    val day: String,
    val offset: Int,
    val program: Program,
)


fun Program.requiredWorkoutDays(): List<Date> {
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

fun Program.lengthDisplay(): String {
    val exerciseEntries = sections.flatMap { it.entries!! } ?: emptyList()
    var exerciseTime = 0f
    for (entry in exerciseEntries) {
        exerciseTime += (entry.sets * entry.timeForUse()).toFloat()
        exerciseTime += (entry.sets * entry.rest).toFloat()
    }
    return (exerciseTime / 60f).toInt().toString()
}

fun ExerciseEntry.timeForUse(): Int {
    return when (basedType) {
        "Time & Reps", "Time" -> time
        else -> 3 * reps
    }
}

