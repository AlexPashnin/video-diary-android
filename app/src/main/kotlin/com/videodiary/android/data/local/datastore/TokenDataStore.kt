package com.videodiary.android.data.local.datastore

import androidx.datastore.core.DataStore
import com.videodiary.android.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStore
    @Inject
    constructor(
        private val dataStore: DataStore<UserPreferences>,
    ) {
        val preferences: Flow<UserPreferences> = dataStore.data

        suspend fun getAccessToken(): String? = dataStore.data.first().accessToken.takeIf { it.isNotBlank() }

        suspend fun getRefreshToken(): String? = dataStore.data.first().refreshToken.takeIf { it.isNotBlank() }

        suspend fun getUserId(): String? = dataStore.data.first().userId.takeIf { it.isNotBlank() }

        suspend fun isTokenExpired(): Boolean {
            val expiresAt = dataStore.data.first().tokenExpiresAtEpochMillis
            return expiresAt == 0L || Instant.now().toEpochMilli() >= expiresAt
        }

        val isDarkModeEnabled: Flow<Boolean> = dataStore.data.map { it.darkModeEnabled }
        val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it.notificationsEnabled }
        val watermarkPosition: Flow<String> = dataStore.data.map { it.watermarkPosition }

        suspend fun saveTokens(
            accessToken: String,
            refreshToken: String,
            expiresIn: Long,
            userId: String,
            userTier: String,
        ) {
            dataStore.updateData { prefs ->
                prefs.toBuilder()
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .setTokenExpiresAtEpochMillis(
                        Instant.now().toEpochMilli() + (expiresIn * 1000),
                    )
                    .setUserId(userId)
                    .setUserTier(userTier)
                    .build()
            }
        }

        suspend fun clearTokens() {
            dataStore.updateData { prefs ->
                prefs.toBuilder()
                    .clearAccessToken()
                    .clearRefreshToken()
                    .setTokenExpiresAtEpochMillis(0L)
                    .clearUserId()
                    .clearUserTier()
                    .build()
            }
        }

        suspend fun updateWatermarkPosition(position: String) {
            dataStore.updateData { it.toBuilder().setWatermarkPosition(position).build() }
        }

        suspend fun updateNotificationsEnabled(enabled: Boolean) {
            dataStore.updateData { it.toBuilder().setNotificationsEnabled(enabled).build() }
        }

        suspend fun updateDarkMode(enabled: Boolean) {
            dataStore.updateData { it.toBuilder().setDarkModeEnabled(enabled).build() }
        }

        suspend fun getFcmToken(): String? = dataStore.data.first().fcmToken.takeIf { it.isNotBlank() }

        suspend fun saveFcmToken(token: String) {
            dataStore.updateData { it.toBuilder().setFcmToken(token).build() }
        }
    }
