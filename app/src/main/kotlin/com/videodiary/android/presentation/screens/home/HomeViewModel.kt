package com.videodiary.android.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.model.CalendarMonth
import com.videodiary.android.domain.repository.ClipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val calendarMonth: CalendarMonth? = null,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clipRepository: ClipRepository,
) : ViewModel() {

    companion object {
        const val INITIAL_PAGE = 1200
        private val BASE_YEAR_MONTH: YearMonth = YearMonth.now()
    }

    fun yearMonthForPage(page: Int): YearMonth =
        BASE_YEAR_MONTH.plusMonths((page - INITIAL_PAGE).toLong())

    private val _currentPage = MutableStateFlow(INITIAL_PAGE)

    val calendarState: StateFlow<HomeUiState> = _currentPage
        .flatMapLatest { page ->
            val ym = yearMonthForPage(page)
            // Fire-and-forget network refresh; Room updates the Flow automatically
            viewModelScope.launch {
                runCatching { clipRepository.getCalendar(ym.year, ym.monthValue) }
            }
            clipRepository.observeCalendar(ym.year, ym.monthValue)
                .map { month ->
                    HomeUiState(
                        isLoading = false,
                        calendarMonth = month.takeIf { it.days.isNotEmpty() },
                    )
                }
                .catch { e ->
                    emit(HomeUiState(isLoading = false, error = e.message ?: "Failed to load calendar"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(isLoading = true),
        )

    fun onPageSettled(page: Int) {
        _currentPage.value = page
    }
}
