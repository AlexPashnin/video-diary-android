package com.videodiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.presentation.navigation.NavGraph
import com.videodiary.android.presentation.navigation.Screen
import com.videodiary.android.presentation.screens.splash.AuthState
import com.videodiary.android.presentation.screens.splash.SplashViewModel
import com.videodiary.android.presentation.theme.VideoDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the splash screen visible until we know where to route the user
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.authState.value == AuthState.Loading
        }

        setContent {
            val authState by splashViewModel.authState.collectAsStateWithLifecycle()

            VideoDiaryTheme {
                when (authState) {
                    AuthState.Loading -> { /* Splash screen is visible; render nothing yet */ }
                    AuthState.Authenticated -> NavGraph(startDestination = Screen.Home.route)
                    AuthState.Unauthenticated -> NavGraph(startDestination = Screen.Login.route)
                }
            }
        }
    }
}
