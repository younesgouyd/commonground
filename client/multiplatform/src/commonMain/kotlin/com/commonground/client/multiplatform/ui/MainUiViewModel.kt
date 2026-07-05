package com.commonground.client.multiplatform.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.LogoutUseCase
import com.commonground.client.multiplatform.data.PlatformFileStorage
import com.commonground.client.multiplatform.data.RepoStore
import kotlinx.coroutines.launch

class MainUiViewModel(
    fileStorage: PlatformFileStorage,
    private val onLogout: () -> Unit
) : ViewModel() {
    // I'm using this viewmodel as a dependency container to maintain this RepoStore instance for the lifecycle of the app
    val repoStore = RepoStore(fileStorage, onLogout)

    private val authRepo get() = repoStore.authRepo

    private val _startDestination = mutableStateOf<Route?>(null)
    val startDestination: State<Route?> = _startDestination

    init {
        viewModelScope.launch {
            authRepo.loadFromDisk()
            val tokens = authRepo.loadTokens()
            if (tokens != null) {
                _startDestination.value = Route.Home
            } else {
                _startDestination.value = Route.Login
            }
        }
    }

    /** Must be called after every login so the Ktor Auth plugin loads fresh tokens. */
    fun onLogin() {
        repoStore.resetClient()
    }

    fun logout() {
        viewModelScope.launch {
            LogoutUseCase(repoStore.authRepo, repoStore.userRepo).execute()
            repoStore.resetClient()
            onLogout()
        }
    }
}