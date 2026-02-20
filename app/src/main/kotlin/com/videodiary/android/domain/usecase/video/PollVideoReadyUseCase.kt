package com.videodiary.android.domain.usecase.video

import com.videodiary.android.domain.model.Video
import com.videodiary.android.domain.model.VideoStatus
import com.videodiary.android.domain.repository.VideoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PollVideoReadyUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {
    operator fun invoke(videoId: String): Flow<Video> = flow {
        while (true) {
            val video = videoRepository.getVideo(videoId)
            emit(video)
            if (video.status == VideoStatus.READY ||
                video.status == VideoStatus.CLIP_EXTRACTED ||
                video.status == VideoStatus.FAILED
            ) break
            delay(POLL_INTERVAL_MS)
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 3_000L
    }
}
