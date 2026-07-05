package com.commonground.client.multiplatform

import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo

class LogoutUseCase(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
) {
    suspend fun execute() {
        // Clear local tokens FIRST — this prevents the server call from
        // triggering a 401→refresh cycle that could re-save tokens.
        val refreshToken = authRepo.loadTokens()?.refreshToken
        authRepo.clearTokens()
        // Best-effort: notify server to invalidate the refresh token
        if (refreshToken != null) {
            try {
                userRepo.logout(refreshToken)
            } catch (_: Exception) { }
        }
    }
}