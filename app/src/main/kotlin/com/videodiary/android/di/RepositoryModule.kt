package com.videodiary.android.di

import com.videodiary.android.data.repository.AuthRepositoryImpl
import com.videodiary.android.data.repository.ClipRepositoryImpl
import com.videodiary.android.data.repository.CompilationRepositoryImpl
import com.videodiary.android.data.repository.VideoRepositoryImpl
import com.videodiary.android.domain.repository.AuthRepository
import com.videodiary.android.domain.repository.ClipRepository
import com.videodiary.android.domain.repository.CompilationRepository
import com.videodiary.android.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository

    @Binds
    @Singleton
    abstract fun bindClipRepository(impl: ClipRepositoryImpl): ClipRepository

    @Binds
    @Singleton
    abstract fun bindCompilationRepository(impl: CompilationRepositoryImpl): CompilationRepository
}
