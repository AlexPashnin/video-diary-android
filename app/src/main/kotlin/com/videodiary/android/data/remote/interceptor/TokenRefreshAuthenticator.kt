package com.videodiary.android.data.remote.interceptor

import com.videodiary.android.data.local.datastore.TokenDataStore
import com.videodiary.android.data.remote.api.AuthApi
import com.videodiary.android.data.remote.dto.auth.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenRefreshAuthenticator
    @Inject
    constructor(
        // Provider<AuthApi> breaks circular dependency (NetworkModule → AuthApi → Authenticator → NetworkModule)
        private val authApiProvider: Provider<AuthApi>,
        private val tokenDataStore: TokenDataStore,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            // Don't retry if the refresh endpoint itself fails
            if (response.request.url.encodedPath.contains("auth/refresh")) return null

            // Stop after the first retry to avoid infinite loops
            if (response.responseCount > 1) return null

            val refreshToken = runBlocking { tokenDataStore.getRefreshToken() } ?: return null

            return try {
                val authResponse =
                    runBlocking {
                        authApiProvider.get().refresh(RefreshRequest(refreshToken))
                    }
                runBlocking {
                    tokenDataStore.saveTokens(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken,
                        expiresIn = authResponse.expiresIn,
                        userId = authResponse.user.id,
                        userTier = authResponse.user.tier,
                    )
                }
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${authResponse.accessToken}")
                    .header("X-User-Id", authResponse.user.id)
                    .build()
            } catch (e: Exception) {
                runBlocking { tokenDataStore.clearTokens() }
                null
            }
        }

        private val Response.responseCount: Int
            get() {
                var count = 1
                var prior = priorResponse
                while (prior != null) {
                    count++
                    prior = prior.priorResponse
                }
                return count
            }
    }
