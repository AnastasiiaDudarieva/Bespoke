package com.bespoke.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Equipment(
    @DocumentId
    var id: String = "",

    @get:PropertyName("label") @set:PropertyName("label")
    var label: String = "",

    @get:PropertyName("value") @set:PropertyName("value")
    var value: String = "",

    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = false,

    @get:PropertyName("hasWeights") @set:PropertyName("hasWeights")
    var hasWeights: Boolean = false
) {
    companion object {
        val weightEquipmentIds = setOf(
            "Ae9nLGXCZNBoPUgo1074",
            "BMXTeBmwk8ozNyKzFSM3",
            "ELQpKUyGyPJYFon4OKy",
            "GYoyGmYqC2d2oKkSpwpa",
            "LNpustgG3Ebs52jYv3mK",
            "MXyy0XANnTgBk8Lljkot",
            "NSg15hefjstdWAKsGznq",
            "VouCLHzWQ5CZH3gAyAuW",
            "XIG7g30IOSnsazTutXsT",
            "eD5kmtpZRuYdFdc227h6",
            "qVcEDamOJFoSnBLFScXG",
            "rITY8YCxCtR7jqlcfcsJ",
            "rnCOWWOMhDZrzAksczZ",
            "rxMdURj50zvBT0vDHV9f",
            "td2ahwaTAnuvz1p52mtA"
        )
    }
}
