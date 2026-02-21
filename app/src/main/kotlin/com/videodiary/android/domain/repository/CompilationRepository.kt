package com.videodiary.android.domain.repository

import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.model.CompilationProgress
import com.videodiary.android.domain.model.CompilationStatus
import com.videodiary.android.domain.model.QualityOption
import com.videodiary.android.domain.model.WatermarkPosition
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CompilationRepository {
    suspend fun createCompilation(
        startDate: LocalDate,
        endDate: LocalDate,
        quality: QualityOption,
        watermarkPosition: WatermarkPosition,
        clipIds: List<String>,
    ): Compilation

    suspend fun getCompilation(compilationId: String): Compilation
    suspend fun getCompilationStatus(compilationId: String): CompilationProgress
    suspend fun listCompilations(status: CompilationStatus? = null, page: Int = 0): List<Compilation>
    suspend fun deleteCompilation(compilationId: String)
    fun observeCompilationProgress(compilationId: String): Flow<CompilationProgress>
    suspend fun getDownloadUrl(compilationId: String): String
}
