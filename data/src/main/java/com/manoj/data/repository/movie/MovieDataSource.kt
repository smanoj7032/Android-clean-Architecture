package com.manoj.data.repository.movie

import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.MoviesEntity
import com.manoj.domain.entities.PopularMoviesEntity
import com.manoj.domain.util.Result

interface MovieDataSource {
        suspend fun getMovies(page: Int? ): Result<MoviesEntity>
        suspend fun getPopularMovies(page: Int?): Result<PopularMoviesEntity>
        suspend fun getMovie(movieId: Int): Result<MovieEntity>
        suspend fun search(query: String, page: Int, limit: Int): Result<PopularMoviesEntity>

}