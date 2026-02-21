package com.videodiary.android.data.remote.mapper

import com.videodiary.android.data.remote.dto.video.VideoResponseDto
import com.videodiary.android.domain.model.VideoStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class VideoMapperTest {

    private val baseDto = VideoResponseDto(
        id = "vid1",
        userId = "user1",
        date = "2024-06-15",
        status = "READY",
        fileSize = 2048L,
        durationSeconds = 45.0,
        spriteSheetUrl = "https://cdn.example.com/sprite.jpg",
        waveformUrl = null,
        videoUrl = "https://cdn.example.com/video.mp4",
        createdAt = "2024-06-15T10:00:00Z",
        updatedAt = "2024-06-15T10:05:00Z",
    )

    @Test
    fun `toDomain maps id and userId`() {
        val domain = baseDto.toDomain()
        assertEquals("vid1", domain.id)
        assertEquals("user1", domain.userId)
    }

    @Test
    fun `toDomain parses date as LocalDate`() {
        val domain = baseDto.toDomain()
        assertEquals(LocalDate.of(2024, 6, 15), domain.date)
    }

    @Test
    fun `toDomain maps status enum`() {
        val domain = baseDto.toDomain()
        assertEquals(VideoStatus.READY, domain.status)
    }

    @Test
    fun `toDomain maps optional nullable fields`() {
        val domain = baseDto.toDomain()
        assertEquals(2048L, domain.fileSize)
        assertEquals(45.0, domain.durationSeconds)
        assertEquals("https://cdn.example.com/sprite.jpg", domain.spriteSheetUrl)
        assertNull(domain.waveformUrl)
        assertEquals("https://cdn.example.com/video.mp4", domain.videoUrl)
    }

    @Test
    fun `toDomain with all nullable fields null`() {
        val dto = baseDto.copy(
            fileSize = null,
            durationSeconds = null,
            spriteSheetUrl = null,
            waveformUrl = null,
            videoUrl = null,
        )
        val domain = dto.toDomain()
        assertNull(domain.fileSize)
        assertNull(domain.durationSeconds)
        assertNull(domain.spriteSheetUrl)
        assertNull(domain.waveformUrl)
        assertNull(domain.videoUrl)
    }

    @Test
    fun `toDomain maps all VideoStatus values`() {
        VideoStatus.entries.forEach { expectedStatus ->
            val domain = baseDto.copy(status = expectedStatus.name).toDomain()
            assertEquals(expectedStatus, domain.status)
        }
    }
}
