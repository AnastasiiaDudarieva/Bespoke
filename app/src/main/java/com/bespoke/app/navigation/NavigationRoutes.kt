package com.bespoke.app.navigation

object Screen {
    const val MEMBER_ROOT = "memberRoot"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ACCOUNT = "account"
    const val EDIT_ACCOUNT = "editAccount"
    const val WEB_VIEW = "webView"
    const val PROGRAMS = "programs"
    const val PROGRAM_OVERVIEW = "program_overview"
    const val EXERCISE = "exercise"


    fun webViewWithUrl(url: String): String {
        return "$WEB_VIEW?url=$url"
    }
}
