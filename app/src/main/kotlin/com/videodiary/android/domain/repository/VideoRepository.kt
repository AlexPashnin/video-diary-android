package com.videodiary.android.domain.repository

import com.videodiary.android.domain.model.Video
import com.videodiary.android.domain.model.VideoStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface VideoRepository {
    suspend fun initiateUpload(date: LocalDate): Pair<String, String> // videoId, uploadUrl
    suspend fun completeUpload(videoId: String)
    suspend fun getVideo(videoId: String): Video
    fun observeVideo(videoId: String): Flow<Video>
    suspend fun listVideos(date: LocalDate? = null, status: VideoStatus? = null, page: Int = 0): List<Video>
    suspend fun deleteVideo(videoId: String)
}
