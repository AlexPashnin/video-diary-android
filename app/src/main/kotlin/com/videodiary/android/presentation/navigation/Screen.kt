package com.videodiary.android.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")

    data object Register : Screen("register")

    // Main
    data object Home : Screen("home")

    data object Upload : Screen("upload")

    data object Record : Screen("record")

    // Clip selection — receives videoId arg
    data object ClipSelect : Screen("clip_select/{videoId}") {
        fun createRoute(videoId: String) = "clip_select/$videoId"
    }

    // Compilation
    data object CompilationCreate : Screen("compilation_create")

    data object CompilationProgress : Screen("compilation_progress/{compilationId}") {
        fun createRoute(compilationId: String) = "compilation_progress/$compilationId"
    }

    data object CompilationHistory : Screen("compilation_history")

    // Player — receives compilationId arg
    data object Player : Screen("player/{compilationId}") {
        fun createRoute(compilationId: String) = "player/$compilationId"
    }

    // Settings
    data object Settings : Screen("settings")
}
