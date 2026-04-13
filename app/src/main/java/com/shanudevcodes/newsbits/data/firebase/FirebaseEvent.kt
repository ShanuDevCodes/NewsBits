package com.shanudevcodes.newsbits.data.firebase

sealed interface FirebaseEvent {
    data class SetUserName(val userName: String) : FirebaseEvent
    data class SetUserEmail(val userEmail: String) : FirebaseEvent
    data class SetUserPassword(val userPassword: String) : FirebaseEvent
    object RegisterUser : FirebaseEvent
    object LoginUser : FirebaseEvent
    object LogoutUser : FirebaseEvent
    object ResetError : FirebaseEvent
    object ResetState : FirebaseEvent
    object UpdateUserName: FirebaseEvent
    object SignInAnonymously : FirebaseEvent
    object ResetPassword : FirebaseEvent
    object DeleteUser : FirebaseEvent
    object SendEmailVerification : FirebaseEvent
    object ReloadUser : FirebaseEvent
    data class LoginWithGoogle(val idToken: String) : FirebaseEvent
}