package com.videodiary.android.data.remote.api

import com.videodiary.android.data.remote.dto.clip.CalendarMonthResponseDto
import com.videodiary.android.data.remote.dto.clip.ClipPageResponse
import com.videodiary.android.data.remote.dto.clip.ClipResponseDto
import com.videodiary.android.data.remote.dto.clip.SelectClipRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClipApi {
    @POST("clips/select")
    suspend fun selectClip(@Body request: SelectClipRequest): ClipResponseDto

    @GET("clips")
    suspend fun listClips(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ClipPageResponse

    @GET("clips/{clipId}")
    suspend fun getClip(@Path("clipId") clipId: String): ClipResponseDto

    @DELETE("clips/{clipId}")
    suspend fun deleteClip(@Path("clipId") clipId: String)

    @GET("clips/calendar")
    suspend fun getCalendar(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): CalendarMonthResponseDto
}
