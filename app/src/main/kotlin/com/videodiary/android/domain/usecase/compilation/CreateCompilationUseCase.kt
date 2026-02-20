package com.videodiary.android.domain.usecase.compilation

import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.model.QualityOption
import com.videodiary.android.domain.model.WatermarkPosition
import com.videodiary.android.domain.repository.CompilationRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateCompilationUseCase @Inject constructor(
    private val compilationRepository: CompilationRepository,
) {
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
        quality: QualityOption,
        watermarkPosition: WatermarkPosition,
        clipIds: List<String>,
    ): Compilation = compilationRepository.createCompilation(
        startDate = startDate,
        endDate = endDate,
        quality = quality,
        watermarkPosition = watermarkPosition,
        clipIds = clipIds,
    )
}
