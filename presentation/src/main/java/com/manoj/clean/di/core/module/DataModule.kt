package com.manoj.clean.di.core.module

import com.manoj.data.api.BaseApi
import com.manoj.data.repository.movie.*
import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieRemote: MovieDataSource,
    ): BaseRepository {
        return BaseRepositoryImpl(movieRemote)
    }


    @Provides
    @Singleton
    fun provideMovieRemoveDataSource(baseApi: BaseApi): MovieDataSource {
        return MovieDataSourceImpl(baseApi)
    }

    @Provides
    fun provideSearchMoviesUseCase(baseRepository: BaseRepository): SearchMovies {
        return SearchMovies(baseRepository)
    }

    @Provides
    fun providePopularMoviesUseCase(baseRepository: BaseRepository): PopularMovies {
        return PopularMovies(baseRepository)
    }

    @Provides
    fun provideGetMovieDetailsUseCase(baseRepository: BaseRepository): GetMovieDetails {
        return GetMovieDetails(baseRepository)
    }


    @Provides
    fun provideGetMovies(baseRepository: BaseRepository): GetMoviesWithSeparators {
        return GetMoviesWithSeparators(baseRepository)
    }


}