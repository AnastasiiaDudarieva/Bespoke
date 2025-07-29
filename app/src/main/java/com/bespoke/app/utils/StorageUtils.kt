package com.bespoke.app.utils

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

suspend fun getFirebaseDownloadUrl(gsPath: String): Uri? {
    val cached = FirebaseStorageUrlCache.get(gsPath)
    if (cached != null) return cached
    val fullPath = if (gsPath.startsWith("gs://")) {
        gsPath
    } else {
        "$gsStorage/$gsPath"
    }

    return try {
        val uri = Firebase.storage.getReferenceFromUrl(fullPath).downloadUrl.await()
        FirebaseStorageUrlCache.set(gsPath, uri)
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

object FirebaseStorageUrlCache {
    private val cache = mutableMapOf<String, Uri>()

    fun get(gsPath: String): Uri? = cache[gsPath]
    fun set(gsPath: String, uri: Uri) {
        cache[gsPath] = uri
    }
}