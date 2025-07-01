package com.bespoke.app.navigation

object Screen {
    const val MemberRoot = "memberRoot"
    const val Profile = "profile"
    const val Settings = "settings"
    const val Account = "account"
    const val WebView = "webview"

    fun webViewWithUrl(url: String): String {
        return "$WebView?url=$url"
    }
}
