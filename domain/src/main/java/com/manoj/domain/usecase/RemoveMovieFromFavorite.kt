package com.manoj.domain.usecase

import com.manoj.domain.repository.MovieRepository

class RemoveMovieFromFavorite(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int) = movieRepository.removeMovieFromFavorite(movieId)
}