package com.videodiary.android.domain.usecase.compilation

import com.videodiary.android.domain.repository.CompilationRepository
import javax.inject.Inject

class DeleteCompilationUseCase @Inject constructor(
    private val compilationRepository: CompilationRepository,
) {
    suspend operator fun invoke(compilationId: String) =
        compilationRepository.deleteCompilation(compilationId)
}
