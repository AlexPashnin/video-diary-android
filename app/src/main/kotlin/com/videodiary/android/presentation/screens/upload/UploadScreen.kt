package com.videodiary.android.presentation.screens.upload

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 4: Implement gallery picker + WorkManager upload pipeline
@Composable
fun UploadScreen(
    onRecordClick: () -> Unit,
    onVideoSelected: (videoId: String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Upload — Gallery Picker (Phase 4)")
    }
}
