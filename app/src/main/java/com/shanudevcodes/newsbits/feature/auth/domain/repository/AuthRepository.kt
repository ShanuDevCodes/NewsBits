package com.shanudevcodes.newsbits.feature.auth.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun loginWithEmailAndPassword(email: String, password: String)
    suspend fun registerWithEmailAndPassword(email: String, password: String, displayName: String)
    suspend fun loginWithGoogle(idToken: String)
    suspend fun logout()
    suspend fun sendEmailVerification()
    suspend fun reloadUser()
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun deleteUser()
    suspend fun signInAnonymously()
    suspend fun updateDisplayName(name: String)
}
