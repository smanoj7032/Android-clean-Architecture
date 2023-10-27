package com.manoj.data.repository.movie

import com.manoj.data.api.BaseApi
import com.manoj.data.entities.toDomain
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.MoviesEntity
import com.manoj.domain.entities.PopularMoviesEntity
import com.manoj.domain.util.Result
import com.manoj.domain.util.performApiCall

class MovieDataSourceImpl(
    private val baseApi: BaseApi
) : MovieDataSource {

    override suspend fun getMovies(page: Int?): Result<MoviesEntity> =
        performApiCall(apiCall = { baseApi.getMovies(page, false) },
            transformer = { it.toDomain() })


    override suspend fun getPopularMovies(page: Int?): Result<PopularMoviesEntity> =
        performApiCall(apiCall = {
            baseApi.getPopularMoviesList(page, false)
        }, transformer = { it.toDomain() })

    override suspend fun getMovie(movieId: Int): Result<MovieEntity> =
        performApiCall(apiCall = { baseApi.getMovie(movieId) }, transformer = { it.toDomain() })


    override suspend fun search(query: String, page: Int, limit: Int): Result<PopularMoviesEntity> =
        performApiCall(apiCall = { baseApi.search("en-US", query, page, false) },
            transformer = { it.toDomain() })
}
