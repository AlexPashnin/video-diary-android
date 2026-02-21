package com.videodiary.android.domain.usecase.clip

import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.model.ClipStatus
import com.videodiary.android.domain.repository.ClipRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PollClipReadyUseCase
    @Inject
    constructor(
        private val clipRepository: ClipRepository,
    ) {
        operator fun invoke(clipId: String): Flow<Clip> =
            flow {
                while (true) {
                    val clip = clipRepository.getClip(clipId)
                    emit(clip)
                    if (clip.status == ClipStatus.READY || clip.status == ClipStatus.FAILED) break
                    delay(POLL_INTERVAL_MS)
                }
            }

        companion object {
            private const val POLL_INTERVAL_MS = 3_000L
        }
    }
