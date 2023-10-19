package com.manoj.clean.ui.feed.usecase

import androidx.paging.PagingData
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesWithSeparators @Inject constructor(
    private val movieRepository: MovieRepository,
) {

    fun movies(pageSize: Int): Flow<PagingData<MovieEntity>> = movieRepository.movies(pageSize)


}
