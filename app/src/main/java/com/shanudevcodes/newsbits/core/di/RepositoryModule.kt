package com.shanudevcodes.newsbits.core.di

import com.shanudevcodes.newsbits.data.savedarticledb.data.repository.ArticleRepositoryImpl
import com.shanudevcodes.newsbits.data.savedarticledb.domain.repository.ArticleRepository
import com.shanudevcodes.newsbits.feature.auth.data.AuthRepositoryImpl
import com.shanudevcodes.newsbits.feature.auth.domain.repository.AuthRepository
import com.shanudevcodes.newsbits.feature.news.data.NewsRepositoryImpl
import com.shanudevcodes.newsbits.feature.news.domain.repository.NewsRepository
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
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    abstract fun bindArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository
}
