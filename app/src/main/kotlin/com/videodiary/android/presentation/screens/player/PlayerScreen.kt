package com.videodiary.android.presentation.screens.player

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.videodiary.android.domain.model.Compilation
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PlayerScreen(
    compilationId: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(compilationId) {
        viewModel.loadCompilation(compilationId)
    }

    when (val s = state) {
        is PlayerState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PlayerState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is PlayerState.Ready -> {
            PlayerContent(
                compilation = s.compilation,
                videoUrl = s.videoUrl,
                onBack = onBack,
                onShare = {
                    // no-op if context unavailable; sharing handled inline in composable
                },
                onDownload = { viewModel.downloadCompilation(s.videoUrl, compilationId) },
            )
        }
    }
}

@Composable
private fun PlayerContent(
    compilation: Compilation,
    videoUrl: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
) {
    val context = LocalContext.current
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    // Poll current playback position to update date watermark
    var positionMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(exoPlayer) {
        while (true) {
            positionMs = exoPlayer.currentPosition
            delay(500)
        }
    }

    val currentDate = remember(positionMs, compilation.startDate) {
        val secondsElapsed = (positionMs / 1000).toInt()
        compilation.startDate.plusDays(secondsElapsed.toLong())
    }
    val dateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Full-screen player
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Back button — top start
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }

        // Share + Download buttons — top end
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
        ) {
            androidx.compose.foundation.layout.Row {
                IconButton(onClick = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, videoUrl)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share compilation"))
                }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                IconButton(onClick = onDownload) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        // Date watermark — bottom start
        Text(
            text = currentDate.format(dateFmt),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 72.dp), // above ExoPlayer controls
        )
    }
}
