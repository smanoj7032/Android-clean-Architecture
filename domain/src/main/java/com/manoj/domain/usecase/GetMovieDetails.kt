package com.manoj.domain.usecase

import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.util.State

class GetMovieDetails(
    private val baseRepository: BaseRepository
) {
    suspend operator fun invoke(movieId: Int): State<MovieEntity> = baseRepository.getMovie(movieId)
}
