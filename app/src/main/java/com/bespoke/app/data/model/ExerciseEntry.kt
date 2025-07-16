package com.bespoke.app.data.model

data class ExerciseEntry(
    val id: String? = null,
    val exerciseId: String? = null,
    val equipmentIds: List<String>? = null,
    val basedType: String? = null,
    val reps: Int? = null,
    val time: Int? = null,
    val weight: Int? = null,
    val sets: Int? = null,
    val rest: Int? = null,
    val comments: String? = null,
//    val mediaList: List<String>? = null,
)