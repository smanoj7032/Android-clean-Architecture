package com.manoj.domain.usecase

import com.manoj.domain.repository.BaseRepository

class AddMovieToFavorite(
    private val baseRepository: BaseRepository
) {
    suspend operator fun invoke(movieId: Int) = baseRepository.addMovieToFavorite(movieId)
}