package com.videodiary.android.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.videodiary.android.presentation.AppViewModel
import com.videodiary.android.presentation.common.OfflineBanner
import com.videodiary.android.presentation.screens.auth.LoginScreen
import com.videodiary.android.presentation.screens.auth.RegisterScreen
import com.videodiary.android.presentation.screens.clipselect.ClipSelectScreen
import com.videodiary.android.presentation.screens.compilation.CompilationCreateScreen
import com.videodiary.android.presentation.screens.compilation.CompilationHistoryScreen
import com.videodiary.android.presentation.screens.compilation.CompilationProgressScreen
import com.videodiary.android.presentation.screens.home.HomeScreen
import com.videodiary.android.presentation.screens.player.PlayerScreen
import com.videodiary.android.presentation.screens.settings.SettingsScreen
import com.videodiary.android.presentation.screens.upload.RecordScreen
import com.videodiary.android.presentation.screens.upload.UploadScreen

private val bottomNavRoutes =
    setOf(
        Screen.Home.route,
        Screen.Upload.route,
        Screen.CompilationHistory.route,
        Screen.Settings.route,
    )

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route,
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes
    val isOnline by appViewModel.isOnline.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { OfflineBanner(isOffline = !isOnline) },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            // Main tabs
            composable(
                route = Screen.Home.route,
                deepLinks = listOf(navDeepLink { uriPattern = "videodiary://home" }),
            ) {
                HomeScreen(
                    onDayWithClipClick = { clipId ->
                        navController.navigate(Screen.Player.createRoute(clipId))
                    },
                    onUploadClick = { navController.navigate(Screen.Upload.route) },
                )
            }
            composable(Screen.Upload.route) {
                UploadScreen(
                    onRecordClick = { navController.navigate(Screen.Record.route) },
                    onVideoSelected = { videoId ->
                        navController.navigate(Screen.ClipSelect.createRoute(videoId))
                    },
                )
            }
            composable(Screen.Record.route) {
                RecordScreen(
                    onVideoRecorded = { videoId ->
                        navController.navigate(Screen.ClipSelect.createRoute(videoId)) {
                            popUpTo(Screen.Upload.route)
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }

            // Clip selection
            composable(
                route = Screen.ClipSelect.route,
                arguments = listOf(navArgument("videoId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "videodiary://clip_select/{videoId}" }),
            ) { entry ->
                ClipSelectScreen(
                    videoId = entry.arguments?.getString("videoId").orEmpty(),
                    onClipSelected = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }

            // Compilations
            composable(Screen.CompilationCreate.route) {
                CompilationCreateScreen(
                    onCompilationCreated = { compilationId ->
                        navController.navigate(Screen.CompilationProgress.createRoute(compilationId)) {
                            popUpTo(Screen.CompilationCreate.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Screen.CompilationProgress.route,
                arguments = listOf(navArgument("compilationId") { type = NavType.StringType }),
            ) { entry ->
                CompilationProgressScreen(
                    compilationId = entry.arguments?.getString("compilationId").orEmpty(),
                    onCompilationReady = { compilationId ->
                        navController.navigate(Screen.Player.createRoute(compilationId)) {
                            popUpTo(Screen.CompilationHistory.route)
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Screen.CompilationHistory.route,
                deepLinks = listOf(navDeepLink { uriPattern = "videodiary://compilation_history" }),
            ) {
                CompilationHistoryScreen(
                    onCompilationClick = { compilationId ->
                        navController.navigate(Screen.Player.createRoute(compilationId))
                    },
                    onCreateClick = { navController.navigate(Screen.CompilationCreate.route) },
                )
            }

            // Player
            composable(
                route = Screen.Player.route,
                arguments = listOf(navArgument("compilationId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "videodiary://player/{compilationId}" }),
            ) { entry ->
                PlayerScreen(
                    compilationId = entry.arguments?.getString("compilationId").orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
