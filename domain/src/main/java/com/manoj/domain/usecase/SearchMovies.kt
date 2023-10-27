package com.manoj.domain.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow

class SearchMovies(
    private val baseRepository: BaseRepository
) {
    operator fun invoke(query: String, pageSize: Int): Flow<PagingData<PopularMovieEntity>> = baseRepository.search(query, pageSize)
}
