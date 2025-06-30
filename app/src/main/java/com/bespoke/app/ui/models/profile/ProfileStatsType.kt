package com.bespoke.app.ui.models.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.bespoke.app.R
import com.bespoke.app.ui.theme.CaloriesColor
import com.bespoke.app.ui.theme.ProgramsColor
import com.bespoke.app.ui.theme.StreakColor
import com.bespoke.app.ui.theme.WorkoutTimeColor

enum class ProfileStatsType(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val backgroundColor: Color,
) {
    Streak(
        R.string.longest_streak,
        R.drawable.ic_bolt_purple,
        StreakColor
    ),
    Programs(
        R.string.programs,
        R.drawable.ic_programs_blue,
        ProgramsColor
    ),
    Calories(
        R.string.calories,
        R.drawable.ic_flame,
        CaloriesColor
    ),
    WorkoutTime(
        R.string.workout_time,
        R.drawable.ic_time_green,
        WorkoutTimeColor
    )
}
