package com.bespoke.app.data.model

import java.util.UUID

data class ProgramSection(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val entries: List<ExerciseEntry> = emptyList()
)