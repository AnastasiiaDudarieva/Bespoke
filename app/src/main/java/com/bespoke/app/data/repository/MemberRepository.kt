package com.bespoke.app.data.repository

import android.graphics.Bitmap
import com.bespoke.app.data.model.Member
import com.bespoke.app.data.services.AuthService
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthService,
) {
    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member
    private var memberListener: ListenerRegistration? = null


    init {
        currentUserId()?.let { uid ->
            CoroutineScope(Dispatchers.IO).launch {
                loadMemberById(uid)
                startListeningForMemberChanges(uid)
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
        stopListeningForMemberChanges()
        auth.logout()
        clearMember()
    }

    suspend fun uploadProfileImage(bitmap: Bitmap): String? {
        val currentUser = auth.currentUserId() ?:return null

        val storageRef = Firebase.storage.reference
            .child("assets/profiles/${currentUser}.jpg")

        val imageData = bitmap.toJpegByteArray(quality = 100) ?: return null
        storageRef.putBytes(imageData).await()
        return storageRef.downloadUrl.await().toString()
    }

    suspend fun updateMemberAvatar(newAvatarUrl: String) {
        val currentUser = auth.currentUserId() ?: return
        val memberRef = firestore.collection("members").document(currentUser)
        try {
            memberRef.update("avatar", newAvatarUrl).await()
            val updatedSnapshot = memberRef.get().await()
            val updatedMember = updatedSnapshot.toObject(Member::class.java)
            _member.emit(updatedMember)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startListeningForMemberChanges(userId: String) {
        memberListener?.remove()

        val docRef = FirebaseFirestore.getInstance()
            .collection("members")
            .document(userId)

        memberListener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                return@addSnapshotListener
            }

            val updatedMember = snapshot.toObject(Member::class.java)
            _member.value = updatedMember
        }
    }

    fun stopListeningForMemberChanges() {
        memberListener?.remove()
        memberListener = null
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
