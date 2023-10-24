package com.manoj.domain.usecase

import com.manoj.domain.repository.BaseRepository

class RemoveMovieFromFavorite(
    private val baseRepository: BaseRepository
) {
    suspend operator fun invoke(movieId: Int) = baseRepository.removeMovieFromFavorite(movieId)
}