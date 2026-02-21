package com.videodiary.android.domain.usecase.video

import com.videodiary.android.domain.repository.VideoRepository
import javax.inject.Inject

class CompleteUploadUseCase
    @Inject
    constructor(
        private val videoRepository: VideoRepository,
    ) {
        suspend operator fun invoke(videoId: String) = videoRepository.completeUpload(videoId)
    }
