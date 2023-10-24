package com.manoj.domain.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteMovies(
    private val baseRepository: BaseRepository
) {
    operator fun invoke(pageSize: Int): Flow<PagingData<MovieEntity>> = baseRepository.favoriteMovies(pageSize)
}