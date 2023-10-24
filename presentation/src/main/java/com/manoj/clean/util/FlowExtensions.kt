package com.manoj.clean.util

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.google.gson.Gson
import com.manoj.domain.util.State
import com.manoj.domain.util.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection


fun <T> singleSharedFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun <T> Flow<State<T>>.emitter(
    mutableStateFlow: MutableStateFlow<State<T>>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    this.onEach { state ->
        when (state.status) {
            Status.SUCCESS -> mutableStateFlow.value = State.success(state.data)
            Status.ERROR -> mutableStateFlow.value = State.error(state.message, true)
            else -> mutableStateFlow.value = State.loading()
        }
    }.catch { throwable ->
        val networkError = parseException(throwable)
        mutableStateFlow.value = State.error(networkError, true)
    }.launchIn(scope)
}


fun <T> StateFlow<State<T>>.customCollector(
    lifecycleOwner: LifecycleOwner,
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T) -> Unit)?,
    onError: ((throwable: Throwable, showError: Boolean) -> Unit)?,
) {
    lifecycleOwner.lifecycleScope.launch {
        collect { state ->
            when (state.status) {
                Status.LOADING -> {
                    onLoading.invoke(true)
                }

                Status.SUCCESS -> {
                    onLoading.invoke(false)
                    onSuccess?.invoke(state.data!!)
                }

                Status.ERROR -> {
                    onLoading.invoke(false)
                    onError?.invoke(Throwable(state.message), state.showErrorView)
                }
            }
        }
    }
}

suspend fun <T> executeApiCall(apiCall: suspend () -> Response<T>): Flow<State<T & Any>> {
    return flow {
        emit(State.loading())
        try {
            val response = apiCall.invoke()
            Log.e("Response-->>", "executeApiCall: ${Gson().toJson(response.message())}")
            if (response.isSuccessful) {
                emit(State.success(response.body()))
            } else {
                if (response.message().isEmpty()) {
                    val errorBody = response.errorBody()
                    val errorMessage = errorBody?.string() ?: response.message()
                    emit(State.error(errorMessage, true))
                } else emit(State.error(response.message(), true))
            }
        } catch (e: Exception) {
            emit(State.error(e.message, true))
        }
    }.flowOn(Dispatchers.IO)
}

fun <T : Any> fetchPagingData(
    pagingSourceFactory: () -> PagingSource<Int, T>
): Flow<PagingData<T>> {
    return Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = pagingSourceFactory
    ).flow.flowOn(Dispatchers.IO)
}


private fun parseException(it: Throwable?): String {
    return when (it) {
        is HttpException -> {
            val exception: HttpException = it
            when (exception.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    val message = getErrorText(it)
                    if (message.contains("Unauthorised")) {
                        "Unauthorised"
                   //     instance.onLogOut()
                    }
                    message
                }

                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    exception.message()
                }

                else -> {
                    getErrorText(it)
                }
            }
        }

        is IOException -> {
            return "Slow or No Internet Access"
        }

        else -> {
            return it?.message.toString()
        }
    }
}

private fun getErrorText(it: HttpException): String {
    var message = "Unknown Error"
    try {
        val errorBody: ResponseBody? = it.response()?.errorBody()
        errorBody?.string()?.apply {
            val obj = JSONObject(this)
            if (obj.has("message")) {
                message = obj.getString("message")
            }
            if (obj.has("error")) {
                message = obj.getString("error")
            }
        }
    } catch (e: Exception) {
        message = it.message().toString()
    }
    return message
}
