package com.manoj.domain.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesWithSeparators(
    private val baseRepository: BaseRepository,
) {
    fun movies(pageSize: Int): Flow<PagingData<MovieEntity>> = baseRepository.movies(pageSize)
}
