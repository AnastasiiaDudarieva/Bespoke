package com.bespoke.app.data.model

import java.util.UUID

data class ExerciseEntry(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val equipmentIds: List<String> = emptyList(),
    val basedType: String = "Reps",
    val reps: Int = 0,
    val time: Int = 0,
    val weight: Int = 0,
    val sets: Int = 0,
    val rest: Int = 0,
    val comments: String? = null,
    val mediaList: List<String>? = null
)