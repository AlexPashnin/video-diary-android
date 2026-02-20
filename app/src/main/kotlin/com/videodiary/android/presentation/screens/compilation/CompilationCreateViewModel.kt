package com.videodiary.android.presentation.screens.compilation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.model.QualityOption
import com.videodiary.android.domain.model.WatermarkPosition
import com.videodiary.android.domain.repository.ClipRepository
import com.videodiary.android.domain.usecase.compilation.CreateCompilationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CompilationCreateState(
    val startDate: LocalDate = LocalDate.now().minusMonths(1),
    val endDate: LocalDate = LocalDate.now(),
    val quality: QualityOption = QualityOption.Q_1080P,
    val watermarkPosition: WatermarkPosition = WatermarkPosition.BOTTOM_RIGHT,
    val clipIds: List<String> = emptyList(),
    val isLoadingClips: Boolean = false,
    val isCreating: Boolean = false,
    val createdId: String? = null,
    val error: String? = null,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CompilationCreateViewModel @Inject constructor(
    private val createCompilationUseCase: CreateCompilationUseCase,
    private val clipRepository: ClipRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CompilationCreateState())
    val state: StateFlow<CompilationCreateState> = _state.asStateFlow()

    init {
        // Auto-load clip IDs when date range changes
        _state
            .map { it.startDate to it.endDate }
            .distinctUntilChanged()
            .debounce(400)
            .flatMapLatest { (start, end) ->
                if (!end.isBefore(start)) {
                    _state.update { it.copy(isLoadingClips = true, clipIds = emptyList()) }
                    flow {
                        emit(loadClipsForRange(start, end))
                    }.catch { emit(emptyList()) }
                } else {
                    flowOf(emptyList())
                }
            }
            .onEach { ids ->
                _state.update { it.copy(clipIds = ids, isLoadingClips = false) }
            }
            .launchIn(viewModelScope)
    }

    fun setStartDate(date: LocalDate) {
        _state.update { s ->
            val end = if (date.isAfter(s.endDate)) date else s.endDate
            s.copy(startDate = date, endDate = end)
        }
    }

    fun setEndDate(date: LocalDate) {
        _state.update { s ->
            val start = if (date.isBefore(s.startDate)) date else s.startDate
            s.copy(startDate = start, endDate = date)
        }
    }

    fun setQuality(quality: QualityOption) {
        _state.update { it.copy(quality = quality) }
    }

    fun setWatermarkPosition(position: WatermarkPosition) {
        _state.update { it.copy(watermarkPosition = position) }
    }

    fun createCompilation() {
        val s = _state.value
        if (s.clipIds.isEmpty()) {
            _state.update { it.copy(error = "No clips found in selected date range.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, error = null) }
            try {
                val compilation = createCompilationUseCase(
                    startDate = s.startDate,
                    endDate = s.endDate,
                    quality = s.quality,
                    watermarkPosition = s.watermarkPosition,
                    clipIds = s.clipIds,
                )
                _state.update { it.copy(isCreating = false, createdId = compilation.id) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isCreating = false, error = e.message ?: "Failed to create compilation")
                }
            }
        }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private suspend fun loadClipsForRange(start: LocalDate, end: LocalDate): List<String> {
        val clipIds = mutableListOf<String>()
        var current = YearMonth.of(start.year, start.month)
        val endMonth = YearMonth.of(end.year, end.month)
        while (!current.isAfter(endMonth)) {
            val calendar = clipRepository.getCalendar(current.year, current.monthValue)
            calendar.days
                .filter { it.hasClip && it.clipId != null &&
                    !it.date.isBefore(start) && !it.date.isAfter(end) }
                .mapNotNull { it.clipId }
                .let { clipIds.addAll(it) }
            current = current.plusMonths(1)
        }
        return clipIds
    }
}
