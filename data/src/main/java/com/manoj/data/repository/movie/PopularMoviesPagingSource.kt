package com.manoj.data.repository.movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.util.Status

private const val STARTING_PAGE = 1

class PopularMoviesPagingSource(
    private val remote: MovieDataSource
) : PagingSource<Int, PopularMovieEntity>() {
    override fun getRefreshKey(state: PagingState<Int, PopularMovieEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PopularMovieEntity> {
        val page = params.key ?: STARTING_PAGE
        Log.e("Page---->>", "load: $page")
        val result = remote.getPopularMovies(page)
        return when (result.status) {
            Status.SUCCESS -> LoadResult.Page(
                data = result.data?.results ?: emptyList(),
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (result.data?.results?.isEmpty() == true) null else page + 1
            )
            else -> LoadResult.Error(Throwable(result.message))
        }
    }
}