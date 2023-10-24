package com.manoj.domain.usecase

import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.util.Result

class CheckFavoriteStatus(
    private val baseRepository: BaseRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Boolean> = baseRepository.checkFavoriteStatus(movieId)
}