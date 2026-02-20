package com.videodiary.android.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.domain.model.CalendarDay
import com.videodiary.android.domain.model.CalendarMonth
import com.videodiary.android.domain.model.ClipStatus
import com.videodiary.android.presentation.common.LoadingIndicator
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDayWithClipClick: (clipId: String) -> Unit,
    onUploadClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val calendarState by viewModel.calendarState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }

    val pagerState = rememberPagerState(
        initialPage = HomeViewModel.INITIAL_PAGE,
        pageCount = { 2400 },
    )

    // Tell the ViewModel which month the user has settled on
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page -> viewModel.onPageSettled(page) }
    }

    val currentYearMonth = viewModel.yearMonthForPage(pagerState.currentPage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentYearMonth.format(MONTH_FORMATTER),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous month",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next month",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            WeekDayHeader()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                val ym = viewModel.yearMonthForPage(page)
                // Only pass loaded data to the settled page; adjacent pages show structure only
                val monthData = if (page == pagerState.settledPage) calendarState.calendarMonth else null
                val isLoading = page == pagerState.settledPage && calendarState.isLoading

                if (isLoading) {
                    LoadingIndicator()
                } else {
                    CalendarMonthPage(
                        yearMonth = ym,
                        calendarMonth = monthData,
                        today = today,
                        onDayClick = { date, clipId ->
                            when {
                                clipId != null -> onDayWithClipClick(clipId)
                                date == today -> onUploadClick()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    HorizontalDivider()
}

@Composable
private fun CalendarMonthPage(
    yearMonth: YearMonth,
    calendarMonth: CalendarMonth?,
    today: LocalDate,
    onDayClick: (date: LocalDate, clipId: String?) -> Unit,
) {
    val dayDataMap: Map<LocalDate, CalendarDay> =
        calendarMonth?.days?.associateBy { it.date } ?: emptyMap()

    // Calculate grid layout
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    // ISO: Monday=1 → column 0, Sunday=7 → column 6
    val leadingBlanks = firstDay.dayOfWeek.value - 1
    val totalCells = leadingBlanks + daysInMonth
    val totalRows = (totalCells + 6) / 7 // ceiling division

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 8.dp),
    ) {
        repeat(totalRows) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - leadingBlanks + 1

                    if (dayNumber < 1 || dayNumber > daysInMonth) {
                        Box(modifier = Modifier.weight(1f))
                    } else {
                        val date = yearMonth.atDay(dayNumber)
                        CalendarDayCell(
                            date = date,
                            dayData = dayDataMap[date],
                            today = today,
                            modifier = Modifier.weight(1f),
                            onClick = { onDayClick(date, dayDataMap[date]?.clipId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    dayData: CalendarDay?,
    today: LocalDate,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isToday = date == today
    val isFuture = date.isAfter(today)
    val clipReady = dayData?.hasClip == true && dayData.clipStatus == ClipStatus.READY
    val clipExtracting = dayData?.hasClip == true && dayData.clipStatus == ClipStatus.EXTRACTING
    val isClickable = clipReady || isToday

    val numberColor = when {
        clipReady -> MaterialTheme.colorScheme.onPrimaryContainer
        isToday -> MaterialTheme.colorScheme.onSurface
        isFuture -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(
                if (clipReady) Modifier.background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small,
                ) else Modifier
            )
            .then(
                if (isToday) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                ) else Modifier
            )
            .then(
                if (isClickable) Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || clipReady) FontWeight.Bold else FontWeight.Normal,
                color = numberColor,
            )
            // Clip indicator — always reserve the space to keep cell height stable
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .then(
                        when {
                            clipReady -> Modifier.background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape,
                            )
                            clipExtracting -> Modifier.background(
                                MaterialTheme.colorScheme.outline,
                                CircleShape,
                            )
                            else -> Modifier
                        }
                    ),
            )
        }
    }
}

