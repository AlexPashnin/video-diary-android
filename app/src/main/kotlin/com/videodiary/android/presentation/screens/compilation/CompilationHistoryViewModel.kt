package com.videodiary.android.presentation.screens.compilation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.usecase.compilation.DeleteCompilationUseCase
import com.videodiary.android.domain.usecase.compilation.ListCompilationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompilationHistoryState(
    val isLoading: Boolean = true,
    val compilations: List<Compilation> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class CompilationHistoryViewModel
    @Inject
    constructor(
        private val listCompilationsUseCase: ListCompilationsUseCase,
        private val deleteCompilationUseCase: DeleteCompilationUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(CompilationHistoryState())
        val state: StateFlow<CompilationHistoryState> = _state.asStateFlow()

        init {
            loadCompilations()
        }

        fun loadCompilations() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val compilations = listCompilationsUseCase()
                    _state.update { it.copy(isLoading = false, compilations = compilations) }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to load compilations")
                    }
                }
            }
        }

        fun deleteCompilation(compilationId: String) {
            viewModelScope.launch {
                try {
                    deleteCompilationUseCase(compilationId)
                    _state.update { s ->
                        s.copy(compilations = s.compilations.filterNot { it.id == compilationId })
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.message ?: "Failed to delete compilation") }
                }
            }
        }

        fun dismissError() {
            _state.update { it.copy(error = null) }
        }
    }
