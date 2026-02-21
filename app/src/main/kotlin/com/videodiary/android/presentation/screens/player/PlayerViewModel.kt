package com.videodiary.android.presentation.screens.player

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.repository.CompilationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlayerState {
    data object Loading : PlayerState
    data class Ready(
        val compilation: Compilation,
        val videoUrl: String,
    ) : PlayerState
    data class Error(val message: String) : PlayerState
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val compilationRepository: CompilationRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Loading)
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    fun loadCompilation(compilationId: String) {
        viewModelScope.launch {
            _state.value = PlayerState.Loading
            try {
                val compilation = compilationRepository.getCompilation(compilationId)
                val videoUrl = compilationRepository.getDownloadUrl(compilationId)
                _state.value = PlayerState.Ready(compilation, videoUrl)
            } catch (e: Exception) {
                _state.value = PlayerState.Error(e.message ?: "Failed to load compilation")
            }
        }
    }

    fun downloadCompilation(videoUrl: String, compilationId: String) {
        val request = DownloadManager.Request(Uri.parse(videoUrl)).apply {
            setTitle("Video Diary Compilation")
            setDescription("Downloading compilation…")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "compilation_$compilationId.mp4",
            )
            setMimeType("video/mp4")
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}
