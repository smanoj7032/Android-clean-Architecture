package com.manoj.domain.usecase

import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.MovieRepository
import com.manoj.domain.util.Result

class GetMovieDetails(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<MovieEntity> = movieRepository.getMovie(movieId)
}
