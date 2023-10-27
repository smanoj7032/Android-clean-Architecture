package com.manoj.domain.repository

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.util.State
import kotlinx.coroutines.flow.Flow

interface BaseRepository {
    fun movies(pageSize: Int): Flow<PagingData<MovieEntity>>
    fun search(query: String, pageSize: Int): Flow<PagingData<PopularMovieEntity>>
    fun getPopularMovies(pageSize: Int): Flow<PagingData<PopularMovieEntity>>
    suspend fun getMovie(movieId: Int): State<MovieEntity>
}