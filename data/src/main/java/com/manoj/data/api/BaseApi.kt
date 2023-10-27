package com.manoj.data.api

import com.manoj.data.entities.MovieData
import com.manoj.data.entities.MoviesData
import com.manoj.data.entities.PopularMoviesData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BaseApi {

    @GET("movie/top_rated")
    suspend fun getMovies(
        @Query("page") page: Int?,
        @Query("include_adult") include_adult: Boolean
    ): Response<MoviesData>

    @GET("movie/popular")
    suspend fun getPopularMoviesList(
        @Query("page") page: Int?,
        @Query("include_adult") include_adult: Boolean
    ): Response<PopularMoviesData>

    @GET("movie/{movie_id}")
    suspend fun getMovie(@Path("movie_id") id: Int?): Response<MovieData>

    @GET("search/movie")
    suspend fun search(
        @Query("language") language: String,
        @Query("query") search: String?,
        @Query("page") page: Int,
        @Query("include_adult") include_adult: Boolean,
    ): Response<PopularMoviesData>
}