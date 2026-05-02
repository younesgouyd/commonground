package com.commonground.client.multiplatform.ui.destinations.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null
) {
    val isValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

class LoginViewModel(
    private val onLoginSuccess: () -> Unit
) : ViewModel() {
    private val logger = KotlinLogging.logger {}

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null, generalError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val emailErr = if (!current.email.isValidEmail()) "Enter a valid email" else null
        val pwErr = if (current.password.length < 8) "Password must be at least 8 characters" else null
        if (emailErr != null || pwErr != null) {
            _state.update { it.copy(emailError = emailErr, passwordError = pwErr) }
            return
        }

        _state.update { it.copy(isSubmitting = true, generalError = null) }
        viewModelScope.launch {
            // Fake network latency so the loading state is visible during dev.
            delay(800)

            // Hardcoded failure path so we can exercise error UI without a backend.
            if (current.email.equals("fail@test.com", ignoreCase = true)) {
                logger.info { "Simulated login failure for ${current.email}" }
                _state.update {
                    it.copy(isSubmitting = false, generalError = "Invalid email or password")
                }
                return@launch
            }

            logger.info { "Hardcoded login success for ${current.email}" }
            onLoginSuccess()
        }
    }
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
internal fun String.isValidEmail(): Boolean = EMAIL_REGEX.matches(trim())