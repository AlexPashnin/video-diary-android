package com.videodiary.android.presentation.screens.compilation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 6: Implement compilation list with swipe-to-delete
@Composable
fun CompilationHistoryScreen(
    onCompilationClick: (compilationId: String) -> Unit,
    onCreateClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Compilation History (Phase 6)")
    }
}
