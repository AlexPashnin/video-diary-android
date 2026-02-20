package com.videodiary.android.presentation.screens.compilation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.model.CompilationProgress
import com.videodiary.android.domain.model.CompilationStatus
import com.videodiary.android.domain.repository.CompilationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface CompilationProgressState {
    data object Loading : CompilationProgressState
    data class InProgress(val progress: CompilationProgress) : CompilationProgressState
    data class Completed(val compilationId: String) : CompilationProgressState
    data class Error(val message: String) : CompilationProgressState
}

@HiltViewModel
class CompilationProgressViewModel @Inject constructor(
    private val compilationRepository: CompilationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<CompilationProgressState>(CompilationProgressState.Loading)
    val state: StateFlow<CompilationProgressState> = _state.asStateFlow()

    fun startObserving(compilationId: String) {
        compilationRepository.observeCompilationProgress(compilationId)
            .onEach { progress ->
                _state.value = when (progress.status) {
                    CompilationStatus.COMPLETED -> CompilationProgressState.Completed(compilationId)
                    CompilationStatus.FAILED ->
                        CompilationProgressState.Error("Compilation failed. Please try again.")
                    else -> CompilationProgressState.InProgress(progress)
                }
            }
            .catch { e ->
                _state.value =
                    CompilationProgressState.Error(e.message ?: "Failed to load progress")
            }
            .launchIn(viewModelScope)
    }
}
