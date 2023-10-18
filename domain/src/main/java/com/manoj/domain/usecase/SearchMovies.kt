package com.manoj.domain.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class SearchMovies(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(query: String, pageSize: Int): Flow<PagingData<MovieEntity>> = movieRepository.search(query, pageSize)
}
