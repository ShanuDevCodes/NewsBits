package com.shanudevcodes.newsbits.data.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shanudevcodes.newsbits.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FirebaseState())
    val state: StateFlow<FirebaseState> = _state

    private val _currentUser = MutableStateFlow<FirebaseUser?>(FirebaseAuth.getInstance().currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    fun onEvent(event: FirebaseEvent) {
        when (event) {

            FirebaseEvent.UpdateUserName -> {
                _state.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    try {
                        repository.updateDisplayName(_state.value.name)
                        withContext(Dispatchers.Main) {
                            checkLoggedInState()
                            val updated = repository.currentUser
                            _currentUser.value = null
                            _currentUser.value = updated
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isError = true, error = e.message.toString()) }
                    } finally {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }

            FirebaseEvent.SignInAnonymously -> {
                _state.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    try {
                        repository.signInAnonymously()
                        withContext(Dispatchers.Main) { checkLoggedInState() }
                    } catch (e: Exception) {
                        _state.update { it.copy(isError = true, error = e.message.toString()) }
                    } finally {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }

            FirebaseEvent.LoginUser -> {
                if (_state.value.email.isNotBlank() && _state.value.password.isNotBlank()) {
                    _state.update { it.copy(isLoading = true) }
                    viewModelScope.launch {
                        try {
                            repository.loginWithEmailAndPassword(
                                _state.value.email,
                                _state.value.password
                            )
                            withContext(Dispatchers.Main) { checkLoggedInState() }
                            repository.reloadUser()
                        } catch (e: Exception) {
                            _state.update { it.copy(isError = true, error = e.message.toString()) }
                        } finally {
                            _state.update { it.copy(isLoading = false) }
                        }
                    }
                }
            }

            FirebaseEvent.LogoutUser -> {
                if (repository.currentUser != null) {
                    viewModelScope.launch {
                        repository.logout()
                        checkLoggedInState()
                        _state.update { it.copy(isError = true, error = "Logged out successfully") }
                    }
                } else {
                    _state.update { it.copy(isError = true, error = "You are not logged in") }
                }
            }

            FirebaseEvent.RegisterUser -> {
                if (_state.value.email.isNotBlank() && _state.value.password.isNotBlank()) {
                    _state.update { it.copy(isLoading = true) }
                    viewModelScope.launch {
                        try {
                            repository.registerWithEmailAndPassword(
                                _state.value.email,
                                _state.value.password,
                                _state.value.name
                            )
                            withContext(Dispatchers.Main) { checkLoggedInState() }
                            repository.reloadUser()
                        } catch (e: Exception) {
                            _state.update { it.copy(isError = true, error = e.message.toString()) }
                        } finally {
                            _state.update { it.copy(isLoading = false) }
                        }
                    }
                }
            }

            is FirebaseEvent.LoginWithGoogle -> {
                _state.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    try {
                        repository.loginWithGoogle(event.idToken)
                        withContext(Dispatchers.Main) { checkLoggedInState() }
                    } catch (e: Exception) {
                        _state.update { it.copy(isError = true, error = e.message.toString()) }
                    } finally {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }

            is FirebaseEvent.SetUserName -> {
                _state.update { it.copy(name = event.userName) }
            }

            is FirebaseEvent.SetUserEmail -> {
                _state.update { it.copy(email = event.userEmail) }
            }

            is FirebaseEvent.SetUserPassword -> {
                _state.update { it.copy(password = event.userPassword) }
            }

            FirebaseEvent.ResetError -> {
                _state.update { it.copy(isError = false, error = "") }
            }

            FirebaseEvent.ResetState -> {
                _state.update { FirebaseState() }
            }

            FirebaseEvent.ResetPassword -> {
                viewModelScope.launch {
                    if (_state.value.email.isBlank()) {
                        _state.update {
                            it.copy(isError = true, error = "Please provide a valid email address.")
                        }
                        return@launch
                    }
                    try {
                        repository.sendPasswordResetEmail(_state.value.email)
                        _state.update {
                            it.copy(
                                isError = true,
                                error = "If an account exists for this email, a password reset link has been sent."
                            )
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isError = true, error = e.message.toString()) }
                    }
                }
            }

            FirebaseEvent.DeleteUser -> {
                viewModelScope.launch {
                    try {
                        repository.deleteUser()
                        withContext(Dispatchers.Main) { checkLoggedInState() }
                    } catch (e: Exception) {
                        _state.update { it.copy(isError = true, error = e.message.toString()) }
                    }
                }
            }

            FirebaseEvent.SendEmailVerification -> {
                viewModelScope.launch {
                    try { repository.sendEmailVerification() } catch (_: Exception) {}
                }
            }

            FirebaseEvent.ReloadUser -> {
                viewModelScope.launch {
                    try {
                        repository.reloadUser()
                        withContext(Dispatchers.Main) { checkLoggedInState() }
                    } catch (e: Exception) {
                        _state.update { it.copy(isError = true, error = e.message.toString()) }
                    }
                }
            }
        }
    }

    fun checkLoggedInState() {
        _state.update { it.copy(isLoggedIn = repository.currentUser != null) }
    }
}