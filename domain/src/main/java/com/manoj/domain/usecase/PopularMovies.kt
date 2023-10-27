package com.manoj.domain.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow

class PopularMovies(private val baseRepository: BaseRepository) {
    operator fun invoke(pageSize: Int): Flow<PagingData<PopularMovieEntity>> =
        baseRepository.getPopularMovies(pageSize)
}