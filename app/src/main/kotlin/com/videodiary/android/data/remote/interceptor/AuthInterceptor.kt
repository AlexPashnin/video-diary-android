package com.videodiary.android.data.remote.interceptor

import com.videodiary.android.data.local.datastore.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val tokenDataStore: TokenDataStore,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = runBlocking { tokenDataStore.getAccessToken() }
            val userId = runBlocking { tokenDataStore.getUserId() }

            val request =
                chain.request().newBuilder().apply {
                    if (token != null) {
                        addHeader("Authorization", "Bearer $token")
                    }
                    if (userId != null) {
                        addHeader("X-User-Id", userId)
                    }
                }.build()

            return chain.proceed(request)
        }
    }
