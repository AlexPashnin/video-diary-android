package com.videodiary.android.presentation.screens.upload

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.presentation.common.VideoPlayer
import java.io.File
import java.time.LocalDate

private sealed interface RecordState {
    data object PermissionRequired : RecordState

    data object Idle : RecordState

    data object Recording : RecordState

    data class Recorded(val uri: Uri) : RecordState
}

@Composable
fun RecordScreen(
    onVideoRecorded: (videoId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val uploadState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    var recordState by remember { mutableStateOf<RecordState>(RecordState.Idle) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }

    // Check camera permissions on first composition
    LaunchedEffect(Unit) {
        val cameraGranted =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
        val audioGranted =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
        if (!cameraGranted || !audioGranted) {
            recordState = RecordState.PermissionRequired
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val granted = permissions.values.all { it }
            recordState = if (granted) RecordState.Idle else RecordState.PermissionRequired
        }

    // CameraX setup
    val recorder =
        remember {
            Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
        }
    val videoCapture = remember { VideoCapture.withOutput(recorder) }
    val preview = remember { Preview.Builder().build() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(previewView) {
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    val cameraSelector =
        remember(useFrontCamera) {
            if (useFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    LaunchedEffect(context) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            cameraProvider = future.get()
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(cameraProvider, cameraSelector, recordState) {
        if (recordState is RecordState.Idle || recordState is RecordState.Recording) {
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview, videoCapture)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activeRecording?.stop()
            cameraProvider?.unbindAll()
        }
    }

    // Navigate when upload finishes
    LaunchedEffect(uploadState) {
        if (uploadState is UploadState.Ready) {
            onVideoRecorded((uploadState as UploadState.Ready).videoId)
        }
    }

    LaunchedEffect(uploadState) {
        if (uploadState is UploadState.Error) {
            snackbarHostState.showSnackbar((uploadState as UploadState.Error).message)
            viewModel.dismissError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val current = recordState) {
            RecordState.PermissionRequired -> {
                PermissionRequiredContent(
                    onRequestPermissions = {
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                        )
                    },
                    onBack = onBack,
                )
            }

            RecordState.Idle, RecordState.Recording -> {
                // Camera preview
                AndroidView(
                    factory = {
                        previewView.apply {
                            layoutParams =
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                // Top controls
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (current is RecordState.Idle) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                    if (current is RecordState.Idle) {
                        IconButton(onClick = { useFrontCamera = !useFrontCamera }) {
                            Icon(
                                Icons.Default.Cameraswitch,
                                contentDescription = "Flip camera",
                                tint = Color.White,
                            )
                        }
                    }
                }

                // Record button
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .navigationBarsPadding()
                            .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(
                        onClick = {
                            when (current) {
                                RecordState.Idle -> {
                                    val tempFile =
                                        File(
                                            context.cacheDir,
                                            "recording_${System.currentTimeMillis()}.mp4",
                                        )
                                    val outputOptions = FileOutputOptions.Builder(tempFile).build()
                                    activeRecording =
                                        videoCapture.output
                                            .prepareRecording(context, outputOptions)
                                            .withAudioEnabled()
                                            .start(ContextCompat.getMainExecutor(context)) { event ->
                                                when (event) {
                                                    is VideoRecordEvent.Finalize -> {
                                                        if (!event.hasError()) {
                                                            recordState =
                                                                RecordState.Recorded(
                                                                    event.outputResults.outputUri,
                                                                )
                                                        } else {
                                                            recordState = RecordState.Idle
                                                        }
                                                    }
                                                    else -> {}
                                                }
                                            }
                                    recordState = RecordState.Recording
                                }
                                RecordState.Recording -> {
                                    activeRecording?.stop()
                                    activeRecording = null
                                }
                                else -> {}
                            }
                        },
                        modifier = Modifier.size(72.dp),
                    ) {
                        Icon(
                            imageVector =
                                if (current is RecordState.Recording) {
                                    Icons.Default.Stop
                                } else {
                                    Icons.Default.Circle
                                },
                            contentDescription = if (current is RecordState.Recording) "Stop" else "Record",
                            tint = if (current is RecordState.Recording) Color.White else Color.Red,
                            modifier = Modifier.size(64.dp),
                        )
                    }
                }
            }

            is RecordState.Recorded -> {
                when (val upload = uploadState) {
                    is UploadState.Idle, is UploadState.VideoSelected -> {
                        RecordedPreviewContent(
                            uri = current.uri,
                            onConfirm = {
                                viewModel.onVideoSelected(current.uri, LocalDate.now())
                                viewModel.startUpload()
                            },
                            onRetake = {
                                recordState = RecordState.Idle
                                viewModel.resetState()
                            },
                        )
                    }
                    is UploadState.Initiating, is UploadState.Processing -> {
                        ProgressContent(
                            label = if (upload is UploadState.Initiating) "Preparing upload…" else "Processing video…",
                        )
                    }
                    is UploadState.Uploading -> {
                        UploadingContent(progress = upload.progress)
                    }
                    is UploadState.Ready, is UploadState.Error -> { /* handled via LaunchedEffect */ }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
        )
    }
}

@Composable
private fun PermissionRequiredContent(
    onRequestPermissions: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Camera permission required",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Camera and microphone access is needed to record videos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermissions) { Text("Grant Permission") }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBack) { Text("Go Back") }
    }
}

@Composable
private fun RecordedPreviewContent(
    uri: Uri,
    onConfirm: () -> Unit,
    onRetake: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlayer(
            uri = uri,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Use This Video") }
            FilledTonalButton(
                onClick = onRetake,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Retake") }
        }
    }
}

@Composable
private fun UploadingContent(progress: Int) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Uploading…", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "$progress%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProgressContent(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
