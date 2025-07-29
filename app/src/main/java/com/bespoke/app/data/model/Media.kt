package com.bespoke.app.data.model

enum class MediaKind {
    image, video, audio;

    companion object {
        fun fromString(value: String?): MediaKind =
            when (value?.lowercase()) {
                "image" -> image
                "video" -> video
                "audio" -> audio
                else -> image
            }
    }
}

data class Media(
    val id: String? = null,
    val createdAt: Long = System.currentTimeMillis() / 1000,
    val kind: String? = MediaKind.image.toString(),
    val path: String? = "mock/image.jpg",
    val thumbnailPath: String? = null,
    val squarePath: String? = null,
    val lengthSec: Int? = null,
)
