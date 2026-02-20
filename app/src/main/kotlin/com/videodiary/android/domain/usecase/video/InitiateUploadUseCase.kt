package com.videodiary.android.domain.usecase.video

import com.videodiary.android.domain.repository.VideoRepository
import java.time.LocalDate
import javax.inject.Inject

class InitiateUploadUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {
    suspend operator fun invoke(date: LocalDate): Pair<String, String> =
        videoRepository.initiateUpload(date)
}
