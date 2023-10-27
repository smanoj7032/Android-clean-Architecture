package com.manoj.data.repository.movie

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.util.Result
import kotlinx.coroutines.flow.Flow

class BaseRepositoryImpl constructor(
    private val movieDataSource: MovieDataSource,
) : BaseRepository {

    override fun movies(pageSize: Int): Flow<PagingData<MovieEntity>> = Pager(config = PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = false,),
        pagingSourceFactory = { MoviesPagingSource(movieDataSource) }).flow



    override fun search(query: String, pageSize: Int): Flow<PagingData<PopularMovieEntity>> =
        Pager(config = PagingConfig(
            pageSize = pageSize, enablePlaceholders = false
        ), pagingSourceFactory = { SearchMoviePagingSource(query, movieDataSource) }).flow

    override fun getPopularMovies(pageSize: Int): Flow<PagingData<PopularMovieEntity>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { PopularMoviesPagingSource(movieDataSource) }).flow

    override suspend fun getMovie(movieId: Int): Result<MovieEntity> = movieDataSource.getMovie(movieId)
}
