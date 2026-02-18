package com.videodiary.android.presentation.screens.clipselect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 5: Implement sprite-sheet scrubber + 1-second clip selection
@Composable
fun ClipSelectScreen(
    videoId: String,
    onClipSelected: () -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Clip Select — Scrubber (Phase 5)")
    }
}
