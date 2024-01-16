package com.manoj.data.repository.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.data.repository.datasource.DataSource
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.util.Status

private const val STARTING_PAGE_INDEX = 1

class SearchMoviePagingSource(
    private val query: String, private val remote: DataSource
) : PagingSource<Int, PopularMovieEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PopularMovieEntity> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = remote.search(query, page, params.loadSize)
        return when (result.status) {
            Status.SUCCESS -> {
                LoadResult.Page(
                    data = result.data?.results ?: emptyList(),
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (result.data?.results?.isEmpty() == true) null else page + 1
                )
            }

            else -> {
                LoadResult.Error(Throwable(result.message))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PopularMovieEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}