package com.bespoke.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class Provider(
    @DocumentId
    val id: String? = null,

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = 0,

    @get:PropertyName("lastName")
    @set:PropertyName("lastName")
    var lastName: String? = null,

    @get:PropertyName("firstName")
    @set:PropertyName("firstName")
    var firstName: String? = null,

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,

    @get:PropertyName("avatar")
    @set:PropertyName("avatar")
    var avatar: String? = null,

    @get:PropertyName("phone")
    @set:PropertyName("phone")
    var phone: String? = null,

    @get:PropertyName("dateOfBirth")
    @set:PropertyName("dateOfBirth")
    var dateOfBirth: Long? = null,

    @get:PropertyName("locationRef")
    @set:PropertyName("locationRef")
    var locationRef: DocumentReference? = null,

    @get:PropertyName("location")
    @set:PropertyName("location")
    var location: String = "",

    @get:PropertyName("canCreateExercises")
    @set:PropertyName("canCreateExercises")
    var canCreateExercises: Boolean = false,

    @get:PropertyName("isAdmin")
    @set:PropertyName("isAdmin")
    var isAdmin: Boolean = false,

    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true,

//    @get:PropertyName("operatingHours")
//    @set:PropertyName("operatingHours")
//    var operatingHours: ProviderOperatingHours? = null,

    @get:PropertyName("calendarId")
    @set:PropertyName("calendarId")
    var calendarId: String? = null,

    @get:PropertyName("calendarColor")
    @set:PropertyName("calendarColor")
    var calendarColor: String? = "#2C61F2"
) {
    fun fullName(): String = "$firstName $lastName"

    fun hasAccessToCreateExercises(): Boolean = isAdmin || canCreateExercises

//    fun toCompact(): CompactProvider? {
//        val id = id ?: return null
//        return CompactProvider(
//            id = id,
//            lastName = lastName,
//            firstName = firstName,
//            email = email,
//            avatar = avatar,
//            phone = phone,
//            dateOfBirth = dateOfBirth,
//            isActive = isActive,
//            calendarId = calendarId,
//            calendarColor = calendarColor
//        )
//    }
}
