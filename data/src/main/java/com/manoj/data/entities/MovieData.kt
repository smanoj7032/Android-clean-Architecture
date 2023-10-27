package com.manoj.data.entities

import com.google.gson.annotations.SerializedName
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.MoviesEntity
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.entities.PopularMoviesEntity


data class MoviesData(
    val page: Int? = null,
    val results: List<MovieData>? = null,
    val total_pages: Int? = null,
    val total_results: Int? = null
)


data class MovieData(
    val adult: Boolean? = null,
    val backdrop_path: String? = null,
    val genre_ids: List<Int>? = null,
    val id: Int? = null,
    val original_language: String? = null,
    val original_title: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val poster_path: String? = null,
    val release_date: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val vote_average: Double? = null,
    val vote_count: Int? = null
)

fun MovieData.toDomain() = MovieEntity(
    adult = adult,
    backdrop_path = backdrop_path,
    genre_ids = genre_ids,
    id = id,
    original_language = original_language,
    original_title = original_title,
    overview = overview,
    popularity = popularity,
    poster_path = poster_path,
    release_date = release_date,
    title = title,
    video = video,
    vote_average = vote_average,
    vote_count = vote_count
)

fun MoviesData.toDomain() = MoviesEntity(
    page = page,
    results = results?.map { it.toDomain() },
    total_pages = total_pages,
    total_results = total_results
)