package com.commonground.client.multiplatform.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.AuthRepo
import kotlinx.coroutines.launch

class MainUiViewModel(
    private val authRepo: AuthRepo
) : ViewModel() {
    private val _startDestination = mutableStateOf<Route?>(null)
    val startDestination: State<Route?> = _startDestination

    init {
        viewModelScope.launch {
            val tokens = authRepo.loadTokens()
            if (tokens != null) {
                _startDestination.value = Route.Home
            } else {
                _startDestination.value = Route.Login
            }
        }
    }
}