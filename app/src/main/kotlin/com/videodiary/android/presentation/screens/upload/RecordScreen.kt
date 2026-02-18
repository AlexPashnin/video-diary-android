package com.videodiary.android.presentation.screens.upload

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 4: Implement CameraX video recording
@Composable
fun RecordScreen(
    onVideoRecorded: (videoId: String) -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Record — CameraX (Phase 4)")
    }
}
