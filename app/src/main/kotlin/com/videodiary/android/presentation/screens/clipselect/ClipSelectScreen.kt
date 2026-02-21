package com.videodiary.android.presentation.screens.clipselect

import android.net.Uri
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.videodiary.android.domain.model.Video
import com.videodiary.android.presentation.common.VideoPlayer
import java.util.Locale
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipSelectScreen(
    videoId: String,
    onClipSelected: () -> Unit,
    onBack: () -> Unit,
    viewModel: ClipSelectViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
    }

    LaunchedEffect(state) {
        if (state is ClipSelectState.Done) {
            onClipSelected()
        }
    }

    LaunchedEffect(state) {
        if (state is ClipSelectState.Error) {
            snackbarHostState.showSnackbar((state as ClipSelectState.Error).message)
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Your Moment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when (val current = state) {
            is ClipSelectState.Loading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }

            is ClipSelectState.VideoReady -> {
                VideoReadyContent(
                    video = current.video,
                    selectedStartTime = current.selectedStartTime,
                    modifier = Modifier.padding(innerPadding),
                    onScrubberChanged = { viewModel.onScrubberChanged(it) },
                    onConfirm = { viewModel.confirmSelection(videoId) },
                )
            }

            is ClipSelectState.Confirming -> {
                ProcessingContent(
                    label = "Selecting clip…",
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is ClipSelectState.Processing -> {
                ProcessingContent(
                    label = "Extracting your 1-second clip…",
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is ClipSelectState.Done, is ClipSelectState.Error -> { /* handled via LaunchedEffect */ }
        }
    }
}

@Composable
private fun VideoReadyContent(
    video: Video,
    selectedStartTime: Double,
    onScrubberChanged: (Double) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val duration = video.durationSeconds ?: 30.0
    val seekToMillis = (selectedStartTime * 1000).toLong()

    Column(modifier = modifier.fillMaxSize()) {
        // Video player (browse mode — paused, seeking to selected time)
        if (video.videoUrl != null) {
            VideoPlayer(
                uri = Uri.parse(video.videoUrl),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                playWhenReady = false,
                useController = false,
                seekToMillis = seekToMillis,
            )
        } else {
            // Fallback: sprite sheet preview when video URL is unavailable
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (video.spriteSheetUrl != null) {
                    AsyncImage(
                        model = video.spriteSheetUrl,
                        contentDescription = "Video preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        "Video preview unavailable",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Drag to scrub · 1-second clip will be selected",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (video.spriteSheetUrl != null) {
                SpriteSheetScrubber(
                    spriteSheetUrl = video.spriteSheetUrl,
                    duration = duration,
                    selectedStartTime = selectedStartTime,
                    onTimeSelected = onScrubberChanged,
                )
            } else {
                TimeScrubber(
                    duration = duration,
                    selectedStartTime = selectedStartTime,
                    onTimeSelected = onScrubberChanged,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatTime(selectedStartTime),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "/ ${formatTime(duration)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Select This Moment")
            }
        }
    }
}

@Composable
private fun SpriteSheetScrubber(
    spriteSheetUrl: String,
    duration: Double,
    selectedStartTime: Double,
    onTimeSelected: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeMax = (duration - 1.0).coerceAtLeast(1.0)
    val selectedPosition = (selectedStartTime / safeMax).toFloat().coerceIn(0f, 1f)
    var scrubberWidth by remember { mutableFloatStateOf(1f) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .onSizeChanged { scrubberWidth = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(scrubberWidth, safeMax) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val t = (offset.x / scrubberWidth * safeMax).coerceIn(0.0, safeMax)
                            onTimeSelected(t)
                        },
                        onDrag = { change, _ ->
                            val t = (change.position.x / scrubberWidth * safeMax).coerceIn(0.0, safeMax)
                            onTimeSelected(t)
                        },
                    )
                },
    ) {
        // Sprite sheet as background
        AsyncImage(
            model = spriteSheetUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Dim overlay + 1-second selection window
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val selW = (size.width / duration.coerceAtLeast(1.0)).toFloat().coerceAtLeast(4f)
            val selLeft = (selectedPosition * size.width).coerceIn(0f, size.width - selW)

            // Dim left
            if (selLeft > 0f) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.55f),
                    size = Size(selLeft, size.height),
                )
            }
            // Dim right
            val rightStart = selLeft + selW
            if (rightStart < size.width) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.55f),
                    topLeft = Offset(rightStart, 0f),
                    size = Size(size.width - rightStart, size.height),
                )
            }
            // Selection border
            drawRect(
                color = Color.White,
                topLeft = Offset(selLeft, 0f),
                size = Size(selW, size.height),
                style = Stroke(width = 3.dp.toPx()),
            )
        }
    }
}

@Composable
private fun TimeScrubber(
    duration: Double,
    selectedStartTime: Double,
    onTimeSelected: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeMax = (duration - 1.0).coerceAtLeast(1.0)
    val selectedPosition = (selectedStartTime / safeMax).toFloat().coerceIn(0f, 1f)
    var scrubberWidth by remember { mutableFloatStateOf(1f) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(40.dp)
                .onSizeChanged { scrubberWidth = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(scrubberWidth, safeMax) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val t = (offset.x / scrubberWidth * safeMax).coerceIn(0.0, safeMax)
                            onTimeSelected(t)
                        },
                        onDrag = { change, _ ->
                            val t = (change.position.x / scrubberWidth * safeMax).coerceIn(0.0, safeMax)
                            onTimeSelected(t)
                        },
                    )
                },
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val trackY = size.height / 2f
            val trackHeight = 4.dp.toPx()
            val thumbRadius = 10.dp.toPx()
            val progressX = selectedPosition * size.width

            // Track background
            drawRect(
                color = Color.Gray.copy(alpha = 0.4f),
                topLeft = Offset(0f, trackY - trackHeight / 2),
                size = Size(size.width, trackHeight),
            )
            // Track fill
            drawRect(
                color = Color.White,
                topLeft = Offset(0f, trackY - trackHeight / 2),
                size = Size(progressX, trackHeight),
            )
            // Thumb
            drawCircle(color = Color.White, radius = thumbRadius, center = Offset(progressX, trackY))

            // 1-second window indicator
            val selW = (size.width / duration.coerceAtLeast(1.0)).toFloat()
            val selLeft = (progressX).coerceIn(0f, size.width - selW)
            drawRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(selLeft, 0f),
                size = Size(selW, size.height),
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading video…", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ProcessingContent(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatTime(seconds: Double): String {
    val s = floor(seconds).toInt()
    return String.format(Locale.US, "%d:%02d", s / 60, s % 60)
}
