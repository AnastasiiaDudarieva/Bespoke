package com.bespoke.app.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

data class Workout(
    @DocumentId var id: String? = null,
    val startedAt: Int = 0,
    val completedAt: Int? = null,
    val completedExerciseEntries: Map<String, ExerciseFeedback>? = null,
    val exerciseEntryInProgress: ExerciseEntryInProgress = ExerciseEntryInProgress(),
    val programId: String = "",
    val _program: Program? = null,
    val sessionTimeSecs: Int = 0,
    val caloriesBurned: Double = 0.0,
    val experiencePain: Boolean = false,
    val effort: Double? = null,
    val feedback: String? = null,
//    val heartRateData: HeartRateData? = null,
//    val _painLog: PainLog? = null,
//    val mediaList: List<Media>? = null,
    val wasOpenedByProvider: Boolean? = null
) {
    val _workout: _Workout?
        get() = if (id != null && completedAt != null && effort != null) {
            _Workout(id!!, completedAt, effort, programId)
        } else null

//    companion object {
//
//        val sample01 = Workout(
//            id = "sample01",
//            startedAt = ((System.currentTimeMillis() / 1000) - 86400 * 3).toInt(),
//            completedAt = ((System.currentTimeMillis() / 1000) - 86400 * 2).toInt(),
//            exerciseEntryInProgress = ExerciseEntryInProgress(
//                currentEntry = Program.sample.sections.first().entries.first()
//            ),
//            programId = "123",
//            _program = Program.sample,
//            effort = 0.8
//        )
//
//        val sample02 = Workout(
//            id = "sample02",
//            startedAt = ((System.currentTimeMillis() / 1000) - 86400 * 2).toInt(),
//            completedAt = ((System.currentTimeMillis() / 1000) - 86400).toInt(),
//            exerciseEntryInProgress = ExerciseEntryInProgress(
//                currentEntry = Program.sample.sections.first().entries.first()
//            ),
//            programId = "321",
//            _program = Program.sample,
//            effort = 0.2
//        )
//    }
}
data class _Workout(
    val id: String,
    val completedAt: Int,
    val effort: Double,
    val programId: String
)

data class ExerciseFeedback(
    var textMessage: String = "",
//    var media: List<Media> = emptyList(),
    var status: String? = null,
    var totalSets: Int? = null,
    var skipedSets: List<Int> = emptyList(),
    var finishedSets: List<Int> = emptyList()
)

data class ExerciseEntryInProgress(
    val currentEntry: ExerciseEntry? = null,
    val exerciseState: ExerciseState = ExerciseState.setsStart,
    val currentSet: Int = 1,
    val isPaused: Boolean = true,
    val isPausedBtwnRep: Boolean = false,
    val counter: Int = 0
)

enum class ExerciseState {
    setsStart,
    restBtwnSets,
    repsStart,
    restBtwnReps,
    paused,
    finished,
    setsFinished
}
