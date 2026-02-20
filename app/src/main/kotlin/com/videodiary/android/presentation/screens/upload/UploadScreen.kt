package com.videodiary.android.presentation.screens.upload

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.presentation.common.VideoPlayer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val MAX_VIDEO_SIZE_BYTES = 200L * 1024 * 1024 // 200 MB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onRecordClick: () -> Unit,
    onVideoSelected: (videoId: String) -> Unit,
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        if (state is UploadState.Ready) {
            onVideoSelected((state as UploadState.Ready).videoId)
        }
    }

    LaunchedEffect(state) {
        if (state is UploadState.Error) {
            snackbarHostState.showSnackbar((state as UploadState.Error).message)
            viewModel.dismissError()
        }
    }

    LaunchedEffect(validationError) {
        validationError?.let {
            snackbarHostState.showSnackbar(it)
            validationError = null
        }
    }

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val mimeType = context.contentResolver.getType(uri) ?: ""
        if (!mimeType.startsWith("video/")) {
            validationError = "Please select a video file."
            return@rememberLauncherForActivityResult
        }
        val size = context.contentResolver
            .query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getLong(0) else 0L } ?: 0L
        if (size > MAX_VIDEO_SIZE_BYTES) {
            validationError = "Video must be under 200 MB."
            return@rememberLauncherForActivityResult
        }
        viewModel.onVideoSelected(uri)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Upload Video") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when (val currentState = state) {
            is UploadState.Idle -> {
                IdleContent(
                    modifier = Modifier.padding(innerPadding),
                    onPickFromGallery = {
                        mediaPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    onRecordClick = onRecordClick,
                )
            }

            is UploadState.VideoSelected -> {
                VideoSelectedContent(
                    uri = currentState.uri,
                    date = currentState.date,
                    modifier = Modifier.padding(innerPadding),
                    onPickDifferent = {
                        mediaPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    onDateClick = { showDatePicker = true },
                    onUpload = { viewModel.startUpload() },
                )
            }

            is UploadState.Initiating -> {
                ProgressContent(
                    label = "Preparing upload…",
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is UploadState.Uploading -> {
                UploadingContent(
                    progress = currentState.progress,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is UploadState.Processing -> {
                ProgressContent(
                    label = "Processing video…",
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is UploadState.Ready, is UploadState.Error -> { /* handled via LaunchedEffect */ }
        }

        if (showDatePicker) {
            val selectedDate = (state as? UploadState.VideoSelected)?.date ?: LocalDate.now()
            val initialMillis = selectedDate
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            viewModel.setDate(date)
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun IdleContent(
    onPickFromGallery: () -> Unit,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Add today's moment",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Record or pick a video from your gallery. You'll select a 1-second clip next.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onPickFromGallery,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pick from Gallery")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRecordClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Videocam, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Record Video")
        }
    }
}

@Composable
private fun VideoSelectedContent(
    uri: Uri,
    date: LocalDate,
    onPickDifferent: () -> Unit,
    onDateClick: () -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        VideoPlayer(
            uri = uri,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Date for this video", style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = onDateClick) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                }
            }
            Button(
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Upload & Continue")
            }
            TextButton(
                onClick = onPickDifferent,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Pick a different video")
            }
        }
    }
}

@Composable
private fun UploadingContent(
    progress: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
private fun ProgressContent(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
