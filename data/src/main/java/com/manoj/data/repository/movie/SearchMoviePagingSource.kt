package com.manoj.data.repository.movie

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.util.getResult

private const val STARTING_PAGE_INDEX = 1

class SearchMoviePagingSource(
    private val query: String,
    private val remote: MovieDataSource
) : PagingSource<Int, PopularMovieEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PopularMovieEntity> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return remote.search(query, page, params.loadSize).getResult({
            val data = it.data.results ?: emptyList()
            LoadResult.Page(
                data = data,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (it.data.results?.isEmpty() == true) null else page + 1
            )
        }, {
            LoadResult.Error(it.error)
        })
    }

    override fun getRefreshKey(state: PagingState<Int, PopularMovieEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}