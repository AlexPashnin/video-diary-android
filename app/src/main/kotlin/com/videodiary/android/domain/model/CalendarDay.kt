package com.videodiary.android.domain.model

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val hasClip: Boolean,
    val clipId: String?,
    val clipStatus: ClipStatus?,
)

data class CalendarMonth(
    val year: Int,
    val month: Int,
    val days: List<CalendarDay>,
)
