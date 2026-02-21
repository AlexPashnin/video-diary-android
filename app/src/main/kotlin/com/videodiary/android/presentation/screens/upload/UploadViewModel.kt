package com.videodiary.android.presentation.screens.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.videodiary.android.data.worker.UploadWorker
import com.videodiary.android.domain.model.VideoStatus
import com.videodiary.android.domain.usecase.video.InitiateUploadUseCase
import com.videodiary.android.domain.usecase.video.PollVideoReadyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface UploadState {
    data object Idle : UploadState

    data class VideoSelected(val uri: Uri, val date: LocalDate) : UploadState

    data object Initiating : UploadState

    data class Uploading(val progress: Int) : UploadState

    data object Processing : UploadState

    data class Ready(val videoId: String) : UploadState

    data class Error(val message: String) : UploadState
}

@HiltViewModel
class UploadViewModel
    @Inject
    constructor(
        private val initiateUploadUseCase: InitiateUploadUseCase,
        private val pollVideoReadyUseCase: PollVideoReadyUseCase,
        private val workManager: WorkManager,
    ) : ViewModel() {
        private val _state = MutableStateFlow<UploadState>(UploadState.Idle)
        val state: StateFlow<UploadState> = _state.asStateFlow()

        private var selectedUri: Uri? = null
        private var selectedDate: LocalDate = LocalDate.now()

        fun onVideoSelected(
            uri: Uri,
            date: LocalDate = LocalDate.now(),
        ) {
            selectedUri = uri
            selectedDate = date
            _state.value = UploadState.VideoSelected(uri, date)
        }

        fun setDate(date: LocalDate) {
            selectedDate = date
            val uri = selectedUri ?: return
            _state.value = UploadState.VideoSelected(uri, date)
        }

        fun startUpload() {
            val uri = selectedUri ?: return
            viewModelScope.launch {
                _state.value = UploadState.Initiating
                try {
                    val (videoId, uploadUrl) = initiateUploadUseCase(selectedDate)

                    val inputData =
                        workDataOf(
                            UploadWorker.KEY_VIDEO_ID to videoId,
                            UploadWorker.KEY_UPLOAD_URL to uploadUrl,
                            UploadWorker.KEY_FILE_URI to uri.toString(),
                        )
                    val workRequest =
                        OneTimeWorkRequestBuilder<UploadWorker>()
                            .setInputData(inputData)
                            .setConstraints(
                                Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build(),
                            )
                            .build()

                    workManager.enqueueUniqueWork(
                        "${UploadWorker.WORK_NAME_PREFIX}$videoId",
                        ExistingWorkPolicy.REPLACE,
                        workRequest,
                    )

                    observeUpload(videoId)
                } catch (e: Exception) {
                    _state.value = UploadState.Error(e.message ?: "Failed to initiate upload")
                }
            }
        }

        private fun observeUpload(videoId: String) {
            workManager.getWorkInfosForUniqueWorkFlow("${UploadWorker.WORK_NAME_PREFIX}$videoId")
                .onEach { workInfoList ->
                    val info = workInfoList.firstOrNull() ?: return@onEach
                    when (info.state) {
                        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> {
                            _state.value = UploadState.Uploading(0)
                        }
                        WorkInfo.State.RUNNING -> {
                            val progress = info.progress.getInt(UploadWorker.KEY_PROGRESS, 0)
                            _state.value = UploadState.Uploading(progress)
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            _state.value = UploadState.Processing
                            pollVideoReady(videoId)
                        }
                        WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                            _state.value = UploadState.Error("Upload failed. Please try again.")
                        }
                    }
                }
                .launchIn(viewModelScope)
        }

        private fun pollVideoReady(videoId: String) {
            pollVideoReadyUseCase(videoId)
                .onEach { video ->
                    when (video.status) {
                        VideoStatus.READY, VideoStatus.CLIP_EXTRACTED -> {
                            _state.value = UploadState.Ready(videoId)
                        }
                        VideoStatus.FAILED -> {
                            _state.value = UploadState.Error("Video processing failed. Please try again.")
                        }
                        else -> { /* still processing */ }
                    }
                }
                .catch { e ->
                    _state.value = UploadState.Error(e.message ?: "Failed to check processing status")
                }
                .launchIn(viewModelScope)
        }

        fun resetState() {
            _state.value = UploadState.Idle
            selectedUri = null
            selectedDate = LocalDate.now()
        }

        fun dismissError() {
            val uri = selectedUri
            _state.value =
                if (uri != null) {
                    UploadState.VideoSelected(uri, selectedDate)
                } else {
                    UploadState.Idle
                }
        }
    }
