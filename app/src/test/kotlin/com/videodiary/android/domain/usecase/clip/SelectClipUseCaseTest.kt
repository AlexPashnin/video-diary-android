package com.videodiary.android.domain.usecase.clip

import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.model.ClipStatus
import com.videodiary.android.domain.repository.ClipRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

class SelectClipUseCaseTest {
    private val clipRepository: ClipRepository = mockk()
    private val useCase = SelectClipUseCase(clipRepository)

    @Test
    fun `invoke delegates to clipRepository with correct arguments`() =
        runTest {
            val date = LocalDate.of(2024, 6, 15)
            val expectedClip =
                Clip(
                    id = "clip1",
                    userId = "user1",
                    videoId = "video1",
                    date = date,
                    status = ClipStatus.PROCESSING,
                    startTimeSeconds = 12.5,
                    objectKey = null,
                    fileSize = null,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                )
            coEvery { clipRepository.selectClip("video1", date, 12.5) } returns expectedClip

            val result = useCase("video1", date, 12.5)

            assertEquals(expectedClip, result)
            coVerify(exactly = 1) { clipRepository.selectClip("video1", date, 12.5) }
        }

    @Test
    fun `invoke propagates exceptions from repository`() =
        runTest {
            val date = LocalDate.of(2024, 6, 15)
            coEvery { clipRepository.selectClip(any(), any(), any()) } throws RuntimeException("Network error")

            var thrownException: Exception? = null
            try {
                useCase("video1", date, 5.0)
            } catch (e: Exception) {
                thrownException = e
            }

            assertEquals("Network error", thrownException?.message)
        }
}
