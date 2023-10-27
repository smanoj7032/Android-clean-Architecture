package com.manoj.domain.util

import retrofit2.Response


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
class ApiException(val code: Int, message: String) : Exception("HTTP $code: $message")

suspend fun <T, R> performApiCall(
    apiCall: suspend () -> Response<T & Any>, transformer: (T) -> R
): State<R> = try {

    val result = apiCall()
    if (result.isSuccessful) {
        if (result.code() == 200) {
            val responseBody = result.body()
            if (responseBody != null) {
                State.success(transformer(responseBody))
            } else {
                State.error("Response body is null", true)
            }
        } else {
            State.error("Unknown error", true)
        }
    } else {
        val errorBody = result.errorBody()
        val errorMessage = errorBody?.string() ?: "Unknown error"
        State.error(ApiException(result.code(), errorMessage).message, true)
    }
} catch (e: Exception) {
    State.error(e.message, true)
}


