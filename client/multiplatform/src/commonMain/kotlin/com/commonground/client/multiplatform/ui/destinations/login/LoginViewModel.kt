package com.commonground.client.multiplatform.ui.destinations.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.core.models.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val login: String = "", // username/email
    val password: String = "",
    val isSubmitting: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null
) {
    val isValid: Boolean
        get() = login.isNotBlank() && password.isNotBlank()
}

class LoginViewModel(
    private val authRepo: AuthRepo,
    private val onLoginSuccess: () -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onUsernameEmailChange(value: String) {
        _state.update { it.copy(login = value, emailError = null, generalError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val emailErr = if (current.login.isBlank()) "Invalid username/email address" else null
        val pwErr = if (current.password.isBlank()) "Password must not be empty" else null
        if (emailErr != null || pwErr != null) {
            _state.update { it.copy(emailError = emailErr, passwordError = pwErr) }
            return
        }

        _state.update { it.copy(isSubmitting = true, generalError = null) }
        viewModelScope.launch {
            try {
                val result = authRepo.login(login = current.login, password = current.password)
                when (result) {
                    is LoginResult.Success -> {
                        onLoginSuccess()
                        _state.update {it.copy(isSubmitting = false, generalError = null) }
                    }
                    is LoginResult.InvalidCredentials -> {
                        _state.update { it.copy(isSubmitting = false, generalError = "Invalid login or password.") }
                    }
                }
            } catch (_: Exception) {
                _state.update {it.copy(isSubmitting = false, generalError = "Something went wrong.") }
            }
        }
    }
}
