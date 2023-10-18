package com.manoj.clean.di.core.module

import com.manoj.data.api.MovieApi
import com.manoj.data.db.favoritemovies.FavoriteMovieDao
import com.manoj.data.db.movies.MovieDao
import com.manoj.data.db.movies.MovieRemoteKeyDao
import com.manoj.data.repository.movie.*
import com.manoj.data.repository.movie.favorite.FavoriteMoviesDataSource
import com.manoj.data.repository.movie.favorite.FavoriteMoviesLocalDataSource
import com.manoj.data.util.DiskExecutor
import com.manoj.domain.repository.MovieRepository
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
    ): MovieRepository {
        return MovieRepositoryImpl(movieRemote, movieLocal, movieRemoteMediator, favoriteLocal)
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
    fun provideMovieRemoveDataSource(movieApi: MovieApi): MovieDataSource.Remote {
        return MovieRemoteDataSource(movieApi)
    }

    @Provides
    fun provideSearchMoviesUseCase(movieRepository: MovieRepository): SearchMovies {
        return SearchMovies(movieRepository)
    }

    @Provides
    fun provideGetMovieDetailsUseCase(movieRepository: MovieRepository): GetMovieDetails {
        return GetMovieDetails(movieRepository)
    }

    @Provides
    fun provideGetFavoriteMoviesUseCase(movieRepository: MovieRepository): GetFavoriteMovies {
        return GetFavoriteMovies(movieRepository)
    }

    @Provides
    fun provideCheckFavoriteStatusUseCase(movieRepository: MovieRepository): CheckFavoriteStatus {
        return CheckFavoriteStatus(movieRepository)
    }

    @Provides
    fun provideAddMovieToFavoriteUseCase(movieRepository: MovieRepository): AddMovieToFavorite {
        return AddMovieToFavorite(movieRepository)
    }

    @Provides
    fun provideRemoveMovieFromFavoriteUseCase(movieRepository: MovieRepository): RemoveMovieFromFavorite {
        return RemoveMovieFromFavorite(movieRepository)
    }
}