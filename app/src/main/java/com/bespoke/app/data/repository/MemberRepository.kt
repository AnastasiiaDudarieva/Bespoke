package com.bespoke.app.data.repository

import android.util.Log
import com.bespoke.app.data.model.Member
import com.bespoke.app.data.services.AuthService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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
    private val auth: AuthService
) {
    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member

    init {
        auth.currentUserId()?.let { uid ->
            CoroutineScope(Dispatchers.IO).launch {
                loadMemberById(uid)
            }
        }
    }

    private suspend fun loadMemberById(userId: String) {
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

    fun logout(){
        auth.logout()
        clearMember()
    }
}
