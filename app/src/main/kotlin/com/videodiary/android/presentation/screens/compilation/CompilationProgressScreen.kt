package com.videodiary.android.presentation.screens.compilation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 6: Implement progress indicator + WebSocket/polling updates
@Composable
fun CompilationProgressScreen(
    compilationId: String,
    onCompilationReady: (compilationId: String) -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Compilation Progress (Phase 6)")
    }
}
