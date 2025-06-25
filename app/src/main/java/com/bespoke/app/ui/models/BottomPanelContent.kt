package com.bespoke.app.ui.models

sealed class BottomPanelContent {
     object Welcome : BottomPanelContent()
     object Login : BottomPanelContent()
     object RequestInvite : BottomPanelContent()
     object ForgotPassword : BottomPanelContent()
}