package com.commonground.client.multiplatform.data

import com.commonground.core.models.TokenPair
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.serialization.removeValue
import kotlinx.serialization.ExperimentalSerializationApi

class SettingsManager {
    companion object {
        private const val TOKENS_TAG = "commonground_auth_tokens"
    }

    private val settings = Settings()

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun getTokens(): TokenPair? {
        return settings.decodeValueOrNull<TokenPair>(TOKENS_TAG)
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun setTokens(tokens: TokenPair) {
        settings.encodeValue(TOKENS_TAG, tokens)
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun clearTokens() {
        settings.removeValue<TokenPair>(TOKENS_TAG)
    }
}