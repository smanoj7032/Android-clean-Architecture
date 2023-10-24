package com.manoj.domain.usecase

import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.util.Result

class GetMovieDetails(
    private val baseRepository: BaseRepository
) {
    suspend operator fun invoke(movieId: Int): Result<MovieEntity> = baseRepository.getMovie(movieId)
}
