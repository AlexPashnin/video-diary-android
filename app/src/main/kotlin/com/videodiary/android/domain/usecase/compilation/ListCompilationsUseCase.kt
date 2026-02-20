package com.videodiary.android.domain.usecase.compilation

import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.repository.CompilationRepository
import javax.inject.Inject

class ListCompilationsUseCase @Inject constructor(
    private val compilationRepository: CompilationRepository,
) {
    suspend operator fun invoke(page: Int = 0): List<Compilation> =
        compilationRepository.listCompilations(page = page)
}
