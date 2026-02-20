package com.videodiary.android.presentation.screens.clipselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.model.ClipStatus
import com.videodiary.android.domain.model.Video
import com.videodiary.android.domain.repository.VideoRepository
import com.videodiary.android.domain.usecase.clip.PollClipReadyUseCase
import com.videodiary.android.domain.usecase.clip.SelectClipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ClipSelectState {
    data object Loading : ClipSelectState
    data class VideoReady(
        val video: Video,
        val selectedStartTime: Double,
    ) : ClipSelectState
    data object Confirming : ClipSelectState
    data object Processing : ClipSelectState
    data object Done : ClipSelectState
    data class Error(val message: String) : ClipSelectState
}

@HiltViewModel
class ClipSelectViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val selectClipUseCase: SelectClipUseCase,
    private val pollClipReadyUseCase: PollClipReadyUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ClipSelectState>(ClipSelectState.Loading)
    val state: StateFlow<ClipSelectState> = _state.asStateFlow()

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            _state.value = ClipSelectState.Loading
            _state.value = try {
                val video = videoRepository.getVideo(videoId)
                ClipSelectState.VideoReady(video, selectedStartTime = 0.0)
            } catch (e: Exception) {
                ClipSelectState.Error(e.message ?: "Failed to load video")
            }
        }
    }

    fun onScrubberChanged(startTimeSeconds: Double) {
        val current = _state.value as? ClipSelectState.VideoReady ?: return
        _state.value = current.copy(selectedStartTime = startTimeSeconds)
    }

    fun confirmSelection(videoId: String) {
        val current = _state.value as? ClipSelectState.VideoReady ?: return
        viewModelScope.launch {
            _state.value = ClipSelectState.Confirming
            try {
                val clip = selectClipUseCase(
                    videoId = videoId,
                    date = current.video.date,
                    startTimeSeconds = current.selectedStartTime,
                )
                _state.value = ClipSelectState.Processing
                pollClipReady(clip.id)
            } catch (e: Exception) {
                _state.value = ClipSelectState.Error(e.message ?: "Failed to select clip")
            }
        }
    }

    private fun pollClipReady(clipId: String) {
        pollClipReadyUseCase(clipId)
            .onEach { clip ->
                when (clip.status) {
                    ClipStatus.READY -> _state.value = ClipSelectState.Done
                    ClipStatus.FAILED -> _state.value =
                        ClipSelectState.Error("Clip extraction failed. Please try again.")
                    else -> { /* still processing */ }
                }
            }
            .catch { e ->
                _state.value = ClipSelectState.Error(e.message ?: "Failed to check clip status")
            }
            .launchIn(viewModelScope)
    }

    fun dismissError() {
        _state.value = ClipSelectState.Loading
    }
}
