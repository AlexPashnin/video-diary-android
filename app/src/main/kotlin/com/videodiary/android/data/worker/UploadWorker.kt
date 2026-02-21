package com.videodiary.android.data.worker

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.videodiary.android.domain.repository.VideoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.InputStream
import javax.inject.Named

@HiltWorker
class UploadWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val videoRepository: VideoRepository,
        @Named("plain") private val okHttpClient: OkHttpClient,
    ) : CoroutineWorker(context, params) {
        private val progressFlow = MutableStateFlow(0)

        override suspend fun doWork(): Result {
            val videoId = inputData.getString(KEY_VIDEO_ID) ?: return Result.failure()
            val uploadUrl = inputData.getString(KEY_UPLOAD_URL) ?: return Result.failure()
            val fileUri = inputData.getString(KEY_FILE_URI) ?: return Result.failure()

            val progressJob =
                progressFlow
                    .distinctUntilChanged()
                    .onEach { progress -> setProgress(workDataOf(KEY_PROGRESS to progress)) }
                    .launchIn(CoroutineScope(Dispatchers.IO + SupervisorJob()))

            return try {
                val uri = Uri.parse(fileUri)
                val contentResolver = applicationContext.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "video/mp4"
                val fileSize = getFileSize(uri, contentResolver)
                val inputStream =
                    contentResolver.openInputStream(uri)
                        ?: return Result.failure()

                val requestBody = createStreamingRequestBody(inputStream, mimeType, fileSize)
                val request =
                    Request.Builder()
                        .url(uploadUrl)
                        .put(requestBody)
                        .build()

                val response =
                    withContext(Dispatchers.IO) {
                        okHttpClient.newCall(request).execute()
                    }

                if (!response.isSuccessful) {
                    response.body?.close()
                    return if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
                }
                response.body?.close()

                videoRepository.completeUpload(videoId)
                Result.success(workDataOf(KEY_VIDEO_ID to videoId))
            } catch (e: Exception) {
                if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
            } finally {
                progressJob.cancel()
            }
        }

        private fun createStreamingRequestBody(
            inputStream: InputStream,
            mimeType: String,
            fileSize: Long,
        ): RequestBody =
            object : RequestBody() {
                override fun contentType() = mimeType.toMediaType()

                override fun contentLength() = fileSize

                override fun writeTo(sink: BufferedSink) {
                    val buffer = ByteArray(BUFFER_SIZE)
                    var totalRead = 0L
                    var lastProgress = -1
                    inputStream.use { stream ->
                        var bytesRead: Int
                        while (stream.read(buffer).also { bytesRead = it } != -1) {
                            sink.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            if (fileSize > 0) {
                                val progress = (totalRead * 100 / fileSize).toInt()
                                if (progress != lastProgress) {
                                    lastProgress = progress
                                    progressFlow.tryEmit(progress)
                                }
                            }
                        }
                    }
                }
            }

        private fun getFileSize(
            uri: Uri,
            contentResolver: ContentResolver,
        ): Long =
            contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getLong(0) else -1L
                } ?: -1L

        companion object {
            const val KEY_VIDEO_ID = "video_id"
            const val KEY_UPLOAD_URL = "upload_url"
            const val KEY_FILE_URI = "file_uri"
            const val KEY_PROGRESS = "progress"
            const val WORK_NAME_PREFIX = "upload_video_"
            private const val MAX_RETRIES = 3
            private const val BUFFER_SIZE = 8 * 1024
        }
    }
