package com.manoj.domain.util

import com.manoj.domain.util.Result.Error
import com.manoj.domain.util.Result.Success

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val error: Throwable) : Result<T>()
}

inline fun <T, R> Result<T>.getResult(
    success: (Success<T>) -> R,
    error: (Error<T>) -> R
): R = when (this) {
    is Success -> success(this)
    is Error -> error(this)
}

inline fun <T> Result<T>.onSuccess(
    block: (T) -> Unit
): Result<T> = if (this is Success) also { block(data) } else this

inline fun <T> Result<T>.onError(
    block: (Throwable) -> Unit
): Result<T> = if (this is Error) also { block(error) } else this



class State<out T>(
    val status: Status, val data: T?, val message: String?, val showErrorView: Boolean
) {
    companion object {
        fun <T> success(data: T?): State<T & Any> {
            return State(Status.SUCCESS, data, null, false)
        }

        fun <T> error(msg: String?, showErrorView: Boolean): State<T> {
            return State(Status.ERROR, null, msg, showErrorView)
        }

        fun <T> loading(): State<T> {
            return State(Status.LOADING, null, null, false)
        }
    }
}


enum class Status { SUCCESS, ERROR, LOADING }
