package com.commonground.client.multiplatform.ui.destinations.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() &&
                username.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank()
}

class SignUpViewModel(
    private val onSignUpSuccess: () -> Unit
) : ViewModel() {
    private val logger = KotlinLogging.logger {}

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onEmailChange(v: String) =
        _state.update { it.copy(email = v, emailError = null, generalError = null) }

    fun onUsernameChange(v: String) =
        _state.update { it.copy(username = v, usernameError = null, generalError = null) }

    fun onPasswordChange(v: String) =
        _state.update { it.copy(password = v, passwordError = null, generalError = null) }

    fun onConfirmPasswordChange(v: String) =
        _state.update { it.copy(confirmPassword = v, confirmPasswordError = null, generalError = null) }

    fun togglePasswordVisibility() =
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun submit() {
        val s = _state.value
        if (s.isSubmitting) return

        val emailErr = if (!EMAIL_REGEX.matches(s.email.trim())) "Enter a valid email" else null
        val userErr = when {
            s.username.length < 3 -> "Username must be at least 3 characters"
            !USERNAME_REGEX.matches(s.username) ->
                "Use only letters, numbers, dots, dashes, and underscores"
            else -> null
        }
        val pwErr = when {
            s.password.length < 8 -> "Password must be at least 8 characters"
            !s.password.any { it.isDigit() } -> "Include at least one number"
            !s.password.any { it.isLetter() } -> "Include at least one letter"
            else -> null
        }
        val confirmErr = if (s.password != s.confirmPassword) "Passwords don't match" else null

        if (emailErr != null || userErr != null || pwErr != null || confirmErr != null) {
            _state.update {
                it.copy(
                    emailError = emailErr,
                    usernameError = userErr,
                    passwordError = pwErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        _state.update { it.copy(isSubmitting = true, generalError = null) }
        viewModelScope.launch {
            delay(800)

            if (s.username.equals("taken", ignoreCase = true)) {
                logger.info { "Simulated signup conflict for ${s.username}" }
                _state.update {
                    it.copy(isSubmitting = false, usernameError = "Username is already taken")
                }
                return@launch
            }

            logger.info { "Hardcoded signup success for ${s.username}" }
            onSignUpSuccess()
        }
    }
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val USERNAME_REGEX = Regex("^[A-Za-z0-9._-]+$")