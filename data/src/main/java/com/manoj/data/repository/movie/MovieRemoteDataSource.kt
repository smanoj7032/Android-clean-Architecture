package com.manoj.data.repository.movie

import com.manoj.data.api.BaseApi
import com.manoj.data.entities.toDomain
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.util.Result

class MovieRemoteDataSource(
    private val baseApi: BaseApi
) : MovieDataSource.Remote {

    override suspend fun getMovies(page: Int?, limit: Int): Result<List<MovieEntity>> = try {
        val result = baseApi.getMovies(page, limit)
        Result.Success(result.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getMovies(movieIds: List<Int>): Result<List<MovieEntity>> = try {
        val result = baseApi.getMovies(movieIds)
        Result.Success(result.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getMovie(movieId: Int): Result<MovieEntity> = try {
        val result = baseApi.getMovie(movieId)
        Result.Success(result.toDomain())
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun search(query: String, page: Int, limit: Int): Result<List<MovieEntity>> = try {
        val result = baseApi.search(query, page, limit)
        Result.Success(result.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e)
    }
}
