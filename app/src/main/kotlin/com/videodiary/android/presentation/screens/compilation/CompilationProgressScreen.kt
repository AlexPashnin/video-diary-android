package com.videodiary.android.presentation.screens.compilation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.domain.model.CompilationProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompilationProgressScreen(
    compilationId: String,
    onCompilationReady: (compilationId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: CompilationProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(compilationId) {
        viewModel.startObserving(compilationId)
    }

    LaunchedEffect(state) {
        if (state is CompilationProgressState.Error) {
            snackbarHostState.showSnackbar((state as CompilationProgressState.Error).message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creating Compilation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (val current = state) {
                is CompilationProgressState.Loading -> {
                    CircularProgressIndicator()
                }

                is CompilationProgressState.InProgress -> {
                    InProgressContent(progress = current.progress)
                }

                is CompilationProgressState.Completed -> {
                    CompletedContent(
                        onPlay = { onCompilationReady(current.compilationId) },
                    )
                }

                is CompilationProgressState.Error -> {
                    ErrorContent(
                        message = current.message,
                        onRetry = { viewModel.startObserving(compilationId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun InProgressContent(progress: CompilationProgress) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val fraction = (progress.percentComplete ?: 0) / 100f
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { fraction },
                modifier = Modifier.size(120.dp),
                strokeWidth = 8.dp,
            )
            Text(
                text = "${progress.percentComplete ?: 0}%",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Text(
            text = if (progress.currentClip != null) {
                "Clip ${progress.currentClip} of ${progress.clipCount}"
            } else {
                "Processing ${progress.clipCount} clips…"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CompletedContent(onPlay: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text("Compilation Ready!", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onPlay) { Text("Play Compilation") }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}
