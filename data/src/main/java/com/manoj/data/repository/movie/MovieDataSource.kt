package com.manoj.data.repository.movie

import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.MoviesEntity
import com.manoj.domain.entities.PopularMoviesEntity
import com.manoj.domain.util.State

interface MovieDataSource {
    suspend fun getMovies(page: Int?): State<MoviesEntity>
    suspend fun getPopularMovies(page: Int?): State<PopularMoviesEntity>
    suspend fun getMovie(movieId: Int): State<MovieEntity>
    suspend fun search(query: String, page: Int, limit: Int): State<PopularMoviesEntity>

}