package com.manoj.data.repository.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.manoj.data.repository.pagingsource.MoviesPagingSource
import com.manoj.data.repository.pagingsource.PopularMoviesPagingSource
import com.manoj.data.repository.pagingsource.SearchMoviePagingSource
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.util.State
import kotlinx.coroutines.flow.Flow

class BaseRepositoryImpl constructor(
    private val dataSource: DataSource,
) : BaseRepository {

    override fun movies(pageSize: Int): Flow<PagingData<MovieEntity>> = Pager(config = PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = false,
    ),
        pagingSourceFactory = { MoviesPagingSource(dataSource) }).flow


    override fun search(query: String, pageSize: Int): Flow<PagingData<PopularMovieEntity>> =
        Pager(config = PagingConfig(
            pageSize = pageSize, enablePlaceholders = false
        ), pagingSourceFactory = { SearchMoviePagingSource(query, dataSource) }).flow

    override fun getPopularMovies(pageSize: Int): Flow<PagingData<PopularMovieEntity>> = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { PopularMoviesPagingSource(dataSource) }).flow

    override suspend fun getMovie(movieId: Int): State<MovieEntity> = dataSource.getMovie(movieId)
}
