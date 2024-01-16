package com.manoj.data.repository.datasource

import com.manoj.data.api.BaseApi
import com.manoj.data.entities.toDomain
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.MoviesEntity
import com.manoj.domain.entities.PopularMoviesEntity
import com.manoj.domain.util.State
import com.manoj.domain.util.performApiCall

class DataSourceImpl(
    private val baseApi: BaseApi
) : DataSource {

    override suspend fun getMovies(page: Int?): State<MoviesEntity> =
        performApiCall(apiCall = { baseApi.getMovies(page, false) },
            transformer = { it.toDomain() })


    override suspend fun getPopularMovies(page: Int?): State<PopularMoviesEntity> =
        performApiCall(apiCall = {
            baseApi.getPopularMoviesList(page, false)
        }, transformer = { it.toDomain() })

    override suspend fun getMovie(movieId: Int): State<MovieEntity> =
        performApiCall(apiCall = { baseApi.getMovie(movieId) }, transformer = { it.toDomain() })


    override suspend fun search(query: String, page: Int, limit: Int): State<PopularMoviesEntity> =
        performApiCall(apiCall = { baseApi.search("en-US", query, page, false) },
            transformer = { it.toDomain() })
}
