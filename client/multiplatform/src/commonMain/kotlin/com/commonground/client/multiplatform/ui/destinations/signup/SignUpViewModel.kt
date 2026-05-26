package com.commonground.client.multiplatform.ui.destinations.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.core.models.SignUpResult
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
        get() = username.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank()
}

class SignUpViewModel(
    private val authRepo: AuthRepo,
    private val onSignUpSuccess: () -> Unit
) : ViewModel() {
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

        val emailErr = if (s.email.isNotBlank() && !EMAIL_REGEX.matches(s.email.trim())) "Enter a valid email" else null
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
            val errors = authRepo.signUp(email = s.email, username = s.username, password = s.password)
            if (errors.isEmpty()) {
                onSignUpSuccess()
            } else {
                var emailErr: String? = null
                var userErr: String? = null
                var pwErr: String? = null
                for (error in errors) {
                    when (error) {
                        SignUpResult.Error.InvalidEmailAddress -> emailErr = "Invalid email address"
                        SignUpResult.Error.InvalidUsername -> userErr = "Username must be at least 3 characters. Use only letters, numbers, dots, dashes, and underscores"
                        SignUpResult.Error.InvalidPassword -> pwErr = "Password must be at least 8 characters, include at least one number, include at least one letter"
                        SignUpResult.Error.UsernameTaken -> userErr = "The username already exists; please choose another one."
                        SignUpResult.Error.EmailTaken -> emailErr = "You already have an account associated with this email address."
                    }
                }
                if (emailErr != null || userErr != null || pwErr != null) {
                    _state.update {
                        it.copy(
                            emailError = emailErr,
                            usernameError = userErr,
                            passwordError = pwErr
                        )
                    }
                }
            }
        }
    }
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val USERNAME_REGEX = Regex("^[A-Za-z0-9._-]+$")