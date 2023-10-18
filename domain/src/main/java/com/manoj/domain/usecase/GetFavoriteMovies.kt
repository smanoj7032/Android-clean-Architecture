package com.manoj.domain.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteMovies(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(pageSize: Int): Flow<PagingData<MovieEntity>> = movieRepository.favoriteMovies(pageSize)
}