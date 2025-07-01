package com.bespoke.app.data.repository

import android.graphics.Bitmap
import com.bespoke.app.data.model.Member
import com.bespoke.app.data.services.AuthService
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthService,
) {
    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member

    init {
        currentUserId()?.let { uid ->
            CoroutineScope(Dispatchers.IO).launch {
                loadMemberById(uid)
            }
        }
    }

    fun currentUserId() = auth.currentUserId()

    suspend fun login(email: String, password: String) = auth.login(email, password)
    suspend fun resetPassword(email: String) = auth.resetPassword(email)

    suspend fun loadMemberById(userId: String) {
        try {
            val document = firestore.collection("members").document(userId).get().await()
            _member.value = document.toObject(Member::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            _member.value = null
        }
    }

    private fun clearMember() {
        _member.value = null
    }

    fun logout() {
        auth.logout()
        clearMember()
    }

    suspend fun uploadProfileImage(bitmap: Bitmap): String? {
        val storageRef = Firebase.storage.reference
            .child("assets/profiles/${UUID.randomUUID()}.jpg")

        val imageData = bitmap.toJpegByteArray(quality = 100) ?: return null
        storageRef.putBytes(imageData).await()
        return storageRef.downloadUrl.await().toString()
    }

    private fun Bitmap.toJpegByteArray(quality: Int = 100): ByteArray? {
        return try {
            val stream = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            stream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
}
