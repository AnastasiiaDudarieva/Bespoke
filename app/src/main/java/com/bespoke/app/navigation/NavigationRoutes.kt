package com.bespoke.app.navigation

object Screen {
    const val MEMBER_ROOT = "memberRoot"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ACCOUNT = "account"
    const val EDIT_ACCOUNT = "editAccount"
    const val WEB_VIEW = "webView"
    const val HOME = "home"
    const val SCHEDULE = "schedule"
    const val PROGRAMS = "programs"
    const val GUIDANCE = "guidance"
    const val PROGRAM_OVERVIEW = "program_overview"
    const val PROGRAM_OVERVIEW_SIMPLE = "program_overview_simple"
    const val EXERCISE = "exercise"
    const val WORKOUT = "workout"
    const val WORKOUT_POST_SESSION = "workout_post_session"


    fun webViewWithUrl(url: String): String {
        return "$WEB_VIEW?url=$url"
    }
}
