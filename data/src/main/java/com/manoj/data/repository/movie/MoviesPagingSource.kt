package com.manoj.data.repository.movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.util.Status

private const val MOVIE_STARTING_PAGE_INDEX = 1

class MoviesPagingSource(
    private val remote: MovieDataSource
) : PagingSource<Int, MovieEntity>() {
    override fun getRefreshKey(state: PagingState<Int, MovieEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieEntity> {
        val page = params.key ?: MOVIE_STARTING_PAGE_INDEX
        Log.e("Page---->>", "load: $page")
        val state = remote.getMovies(page)
        return when (state.status) {
            Status.SUCCESS -> {
                LoadResult.Page(
                    data = state.data?.results ?: emptyList(),
                    prevKey = if (page == MOVIE_STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (state.data?.results?.isEmpty() == true) null else page + 1
                )
            }
            else -> {LoadResult.Error(Throwable(state.message))}
        }

    }
}
