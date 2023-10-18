package com.manoj.domain.usecase

import com.manoj.domain.repository.MovieRepository
import com.manoj.domain.util.Result

class CheckFavoriteStatus(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Boolean> = movieRepository.checkFavoriteStatus(movieId)
}