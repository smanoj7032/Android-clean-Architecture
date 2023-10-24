package com.manoj.clean.di.core.module

import com.manoj.data.api.BaseApi
import com.manoj.data.db.favoritemovies.FavoriteMovieDao
import com.manoj.data.db.movies.MovieDao
import com.manoj.data.db.movies.MovieRemoteKeyDao
import com.manoj.data.repository.movie.*
import com.manoj.data.repository.movie.favorite.FavoriteMoviesDataSource
import com.manoj.data.repository.movie.favorite.FavoriteMoviesLocalDataSource
import com.manoj.data.util.DiskExecutor
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
        movieRemote: MovieDataSource.Remote,
        movieLocal: MovieDataSource.Local,
        movieRemoteMediator: MovieRemoteMediator,
        favoriteLocal: FavoriteMoviesDataSource.Local,
    ): BaseRepository {
        return BaseRepositoryImpl(movieRemote, movieLocal, movieRemoteMediator, favoriteLocal)
    }

    @Provides
    @Singleton
    fun provideMovieLocalDataSource(
        executor: DiskExecutor,
        movieDao: MovieDao,
        movieRemoteKeyDao: MovieRemoteKeyDao,
    ): MovieDataSource.Local {
        return MovieLocalDataSource(executor, movieDao, movieRemoteKeyDao)
    }

    @Provides
    @Singleton
    fun provideMovieMediator(
        movieLocalDataSource: MovieDataSource.Local,
        movieRemoteDataSource: MovieDataSource.Remote
    ): MovieRemoteMediator {
        return MovieRemoteMediator(movieLocalDataSource, movieRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideFavoriteMovieLocalDataSource(
        executor: DiskExecutor,
        favoriteMovieDao: FavoriteMovieDao
    ): FavoriteMoviesDataSource.Local {
        return FavoriteMoviesLocalDataSource(executor, favoriteMovieDao)
    }

    @Provides
    @Singleton
    fun provideMovieRemoveDataSource(baseApi: BaseApi): MovieDataSource.Remote {
        return MovieRemoteDataSource(baseApi)
    }

    @Provides
    fun provideSearchMoviesUseCase(baseRepository: BaseRepository): SearchMovies {
        return SearchMovies(baseRepository)
    }

    @Provides
    fun provideGetMovieDetailsUseCase(baseRepository: BaseRepository): GetMovieDetails {
        return GetMovieDetails(baseRepository)
    }

    @Provides
    fun provideGetFavoriteMoviesUseCase(baseRepository: BaseRepository): GetFavoriteMovies {
        return GetFavoriteMovies(baseRepository)
    }
    @Provides
    fun provideGetMovies(baseRepository: BaseRepository):GetMoviesWithSeparators {
        return GetMoviesWithSeparators(baseRepository)
    }
    @Provides
    fun provideCheckFavoriteStatusUseCase(baseRepository: BaseRepository): CheckFavoriteStatus {
        return CheckFavoriteStatus(baseRepository)
    }

    @Provides
    fun provideAddMovieToFavoriteUseCase(baseRepository: BaseRepository): AddMovieToFavorite {
        return AddMovieToFavorite(baseRepository)
    }

    @Provides
    fun provideRemoveMovieFromFavoriteUseCase(baseRepository: BaseRepository): RemoveMovieFromFavorite {
        return RemoveMovieFromFavorite(baseRepository)
    }
}