package com.videodiary.android.data.repository

import com.videodiary.android.data.remote.api.CompilationApi
import com.videodiary.android.data.remote.dto.compilation.CreateCompilationRequest
import com.videodiary.android.data.remote.mapper.toDomain
import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.model.CompilationProgress
import com.videodiary.android.domain.model.CompilationStatus
import com.videodiary.android.domain.model.QualityOption
import com.videodiary.android.domain.model.WatermarkPosition
import com.videodiary.android.domain.repository.CompilationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompilationRepositoryImpl @Inject constructor(
    private val compilationApi: CompilationApi,
) : CompilationRepository {

    override suspend fun createCompilation(
        startDate: LocalDate,
        endDate: LocalDate,
        quality: QualityOption,
        watermarkPosition: WatermarkPosition,
        clipIds: List<String>,
    ): Compilation = compilationApi.createCompilation(
        CreateCompilationRequest(
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            quality = quality.name,
            watermarkPosition = watermarkPosition.name,
            clipIds = clipIds,
        )
    ).toDomain()

    override suspend fun getCompilation(compilationId: String): Compilation =
        compilationApi.getCompilation(compilationId).toDomain()

    override suspend fun getCompilationStatus(compilationId: String): CompilationProgress =
        compilationApi.getCompilationStatus(compilationId).toDomain()

    override suspend fun listCompilations(status: CompilationStatus?, page: Int): List<Compilation> =
        compilationApi.listCompilations(status = status?.name, page = page).content.map { it.toDomain() }

    override suspend fun deleteCompilation(compilationId: String) {
        compilationApi.deleteCompilation(compilationId)
    }

    override fun observeCompilationProgress(compilationId: String): Flow<CompilationProgress> = flow {
        while (true) {
            val progress = compilationApi.getCompilationStatus(compilationId).toDomain()
            emit(progress)
            if (progress.status == CompilationStatus.COMPLETED || progress.status == CompilationStatus.FAILED) break
            delay(POLL_INTERVAL_MS)
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 3_000L
    }
}
