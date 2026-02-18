package com.videodiary.android.data.repository

import com.videodiary.android.data.local.db.dao.VideoDao
import com.videodiary.android.data.local.db.mapper.toDomain
import com.videodiary.android.data.local.db.mapper.toEntity
import com.videodiary.android.data.remote.api.VideoApi
import com.videodiary.android.data.remote.dto.video.InitiateUploadRequest
import com.videodiary.android.data.remote.mapper.toDomain
import com.videodiary.android.domain.model.Video
import com.videodiary.android.domain.model.VideoStatus
import com.videodiary.android.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoApi: VideoApi,
    private val videoDao: VideoDao,
) : VideoRepository {

    override suspend fun initiateUpload(date: LocalDate): Pair<String, String> {
        val response = videoApi.initiateUpload(InitiateUploadRequest(date.toString()))
        return response.videoId to response.uploadUrl
    }

    override suspend fun completeUpload(videoId: String) {
        videoApi.completeUpload(videoId)
    }

    override suspend fun getVideo(videoId: String): Video {
        val remote = videoApi.getVideo(videoId).toDomain()
        videoDao.upsert(remote.toEntity())
        return remote
    }

    override fun observeVideo(videoId: String): Flow<Video> =
        videoDao.observeById(videoId).filterNotNull().map { it.toDomain() }

    override suspend fun listVideos(
        date: LocalDate?,
        status: VideoStatus?,
        page: Int,
    ): List<Video> {
        val remote = videoApi.listVideos(
            date = date?.toString(),
            status = status?.name,
            page = page,
        ).content.map { it.toDomain() }
        if (page == 0) videoDao.upsertAll(remote.map { it.toEntity() })
        return remote
    }

    override suspend fun deleteVideo(videoId: String) {
        videoApi.deleteVideo(videoId)
        videoDao.deleteById(videoId)
    }
}
