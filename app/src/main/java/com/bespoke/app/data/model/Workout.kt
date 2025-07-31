package com.bespoke.app.data.model

import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Workout(
    @DocumentId var id: String? = null,
    var startedAt: Int = 0,
    var completedAt: Int? = null,
    var completedExerciseEntries: Map<String, ExerciseFeedback> = emptyMap(),
    var exerciseEntryInProgress: ExerciseEntryInProgress? = null,
    var programId: String = "",
    var _program: Program? = null,
    var sessionTimeSecs: Int = 0,
    var caloriesBurned: Double = 0.0,
    var experiencePain: Boolean = false,
    var effort: Double? = null,
    var feedback: String? = null,
    var heartRateData: HeartRateData? = null,
    var _painLog: PainLog? = null,
    var mediaList: List<Media>? = null,
    var wasOpenedByProvider: Boolean? = null,
)

data class ExerciseFeedback(
    var textMessage: String = "",
    var media: List<Media> = emptyList(),
    var status: String? = null,
    var totalSets: Int? = null,
    var skipedSets: List<Int> = emptyList(),
    var finishedSets: List<Int> = emptyList()
)

data class ExerciseEntryInProgress(
    var currentEntry: ExerciseEntry? = null,
    var exerciseState: ExerciseState = ExerciseState.setsStart,
    var currentSet: Int = 1,
    @get:PropertyName("isPaused") @set:PropertyName("isPaused")
    var isPaused: Boolean? = true,
    @get:PropertyName("isPausedBtwnRep") @set:PropertyName("isPausedBtwnRep")
    var isPausedBtwnRep: Boolean? = false,
    var counter: Int = 0,
)

enum class ExerciseState {
    setsStart,
    preActive,
    active,
    preRest,
    rest,
    setsFinished,
}

data class HeartRate(
    val date: Int = 0,
    val min: Double = 0.0,
    val max: Double = 0.0
)

data class HeartRateData(
    var workoutRates: List<HeartRate>? = null
) {
    fun setPerMinute(data: List<Triple<Date, Double, Double>>) {
        workoutRates = data.map { (date, min, max) ->
            HeartRate(date = (date.time / 1000).toInt(), min = min, max = max)
        }
    }
}

data class PainLog(
    @DocumentId var id: String? = null,
    val submittedAt: Int = 0,
    val musclePainData: List<MusclePain> = emptyList(),
    val exerciseEntryIds: List<String>? = null,
    val feedback: String? = null,
    val mediaList: List<Media>? = null,
    val wasOpenedByProvider: Boolean? = null
)

data class MusclePain(
    val part: PainModelPart = PainModelPart.Head,
    val type: String = "",
    val intensity: Double = 0.0,
    val pins: List<Float3> = emptyList()
)
data class Float3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
)

enum class PainModelPart {
    @SerializedName("Ankle_L") Ankle_L,
    @SerializedName("Ankle_R") Ankle_R,
    @SerializedName("Bicep_L") Bicep_L,
    @SerializedName("Bicep_R") Bicep_R,
    @SerializedName("Calve_L") Calve_L,
    @SerializedName("Calve_R") Calve_R,
    @SerializedName("Chest_L") Chest_L,
    @SerializedName("Chest_R") Chest_R,
    @SerializedName("Core_L") Core_L,
    @SerializedName("Core_R") Core_R,
    @SerializedName("Elbow_L") Elbow_L,
    @SerializedName("Elbow_R") Elbow_R,
    @SerializedName("Foot_L") Foot_L,
    @SerializedName("Foot_R") Foot_R,
    @SerializedName("Forearm_L") Forearm_L,
    @SerializedName("Forearm_R") Forearm_R,
    @SerializedName("Genital_Area") Genital_Area,
    @SerializedName("Glute_L") Glute_L,
    @SerializedName("Glute_R") Glute_R,
    @SerializedName("Hamstring_L") Hamstring_L,
    @SerializedName("Hamstring_R") Hamstring_R,
    @SerializedName("Hand_L") Hand_L,
    @SerializedName("Hand_R") Hand_R,
    @SerializedName("Head") Head,
    @SerializedName("Hip_L") Hip_L,
    @SerializedName("Hip_R") Hip_R,
    @SerializedName("Knee_L") Knee_L,
    @SerializedName("Knee_R") Knee_R,
    @SerializedName("Lower_Back_L") Lower_Back_L,
    @SerializedName("Lower_Back_R") Lower_Back_R,
    @SerializedName("Middle_Back_L") Middle_Back_L,
    @SerializedName("Middle_Back_R") Middle_Back_R,
    @SerializedName("Neck") Neck,
    @SerializedName("Oblique_L") Oblique_L,
    @SerializedName("Oblique_R") Oblique_R,
    @SerializedName("Quad_L") Quad_L,
    @SerializedName("Quad_R") Quad_R,
    @SerializedName("Shin_L") Shin_L,
    @SerializedName("Shin_R") Shin_R,
    @SerializedName("Shoulder_L") Shoulder_L,
    @SerializedName("Shoulder_R") Shoulder_R,
    @SerializedName("Tricep_L") Tricep_L,
    @SerializedName("Tricep_R") Tricep_R,
    @SerializedName("Upper_Back_L") Upper_Back_L,
    @SerializedName("Upper_Back_R") Upper_Back_R,
    @SerializedName("Wrist_L") Wrist_L,
    @SerializedName("Wrist_R") Wrist_R
}

fun Workout.isWorkoutComplete(): Boolean {
    val totalExercises = _program?.sections
        ?.flatMap { it.entries?: emptyList() }
        ?.size ?: return false
    val finishedExercises = completedExerciseEntries
        .values
        .count { it.status == ExerciseState.setsFinished.name }

    Log.e("totalExercises", "${totalExercises}")
    Log.e("finishedExercises", "${finishedExercises}")
    return finishedExercises == totalExercises
}