package com.bespoke.app.data.model

import java.util.Date

data class PastWorkout(
    val completedAt: Int,
    val program: Program,
    val didComplete: Boolean,
    val caloriesBurned: Double
)

data class StreakDataStats(
    val dateIsCompleteData: List<DateIsComplete>,
    val longestStreak: Int,
    val currentStreak: Int
) {
    data class DateIsComplete(val date: Date, val isComplete: Boolean)
}
