package com.bespoke.app.data.model

import java.util.UUID

enum class MediaKind {
    IMAGE, VIDEO, AUDIO;

    companion object {
        fun fromString(value: String?): MediaKind =
            when (value?.lowercase()) {
                "image" -> IMAGE
                "video" -> VIDEO
                "audio" -> AUDIO
                else -> IMAGE
            }


    }


}


data class Media(
    val id: String? = null,
    val createdAt: Long = System.currentTimeMillis() / 1000,
    val kind: String? = MediaKind.IMAGE.toString(),
    val path: String? = "mock/image.jpg",
    val thumbnailPath: String? = null,
    val squarePath: String? = null,
    val lengthSec: Int? = null,
)
