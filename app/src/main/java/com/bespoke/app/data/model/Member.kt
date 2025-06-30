package com.bespoke.app.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Member(
    val id: String?=null,
    val firstName: String?=null,
    val lastName: String?=null,
    val email: String?=null,
    val avatar: String? = null,
)