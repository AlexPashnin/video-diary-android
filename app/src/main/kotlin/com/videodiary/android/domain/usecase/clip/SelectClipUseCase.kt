package com.videodiary.android.domain.usecase.clip

import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.repository.ClipRepository
import java.time.LocalDate
import javax.inject.Inject

class SelectClipUseCase
    @Inject
    constructor(
        private val clipRepository: ClipRepository,
    ) {
        suspend operator fun invoke(
            videoId: String,
            date: LocalDate,
            startTimeSeconds: Double,
        ): Clip = clipRepository.selectClip(videoId, date, startTimeSeconds)
    }
