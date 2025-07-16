package com.bespoke.app.data.model

data class ProgramSection(
    val id: String? = null,
    val title: String? = null,
    val entries: List<ExerciseEntry>? = null
)