package com.commonground.client.multiplatform.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.LogoutUseCase
import com.commonground.client.multiplatform.data.RepoStore
import com.commonground.core.models.User
import kotlinx.coroutines.launch

class MainUiViewModel(
    private val onLogout: () -> Unit
) : ViewModel() {
    val repoStore = RepoStore(onLogout)

    private val authRepo get() = repoStore.authRepo

    private val _startDestination = mutableStateOf<Route?>(null)
    val startDestination: State<Route?> = _startDestination

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    init {
        viewModelScope.launch {
            authRepo.loadFromDisk()
            val tokens = authRepo.loadTokens()
            if (tokens != null) {
                _startDestination.value = Route.Home
                _currentUser.value = repoStore.userRepo.getLoggedInUser()
            } else {
                _startDestination.value = Route.Login
            }
        }
    }

    fun onLogin() {
        repoStore.resetClient()
        viewModelScope.launch {
            _currentUser.value = repoStore.userRepo.getLoggedInUser()
        }
    }

    fun logout() {
        viewModelScope.launch {
            LogoutUseCase(repoStore.authRepo, repoStore.userRepo).execute()
            repoStore.resetClient()
            _currentUser.value = null
            onLogout()
        }
    }
}
