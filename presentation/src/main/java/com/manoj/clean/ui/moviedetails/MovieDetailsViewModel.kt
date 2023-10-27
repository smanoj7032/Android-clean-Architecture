package com.manoj.clean.ui.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.usecase.GetMovieDetails
import com.manoj.domain.util.Result
import com.manoj.domain.util.State
import com.manoj.domain.util.Status
import com.manoj.domain.util.onError
import com.manoj.domain.util.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow

class MovieDetailsViewModel @AssistedInject constructor(
    @Assisted private var movieId: Int,
    private val getMovieDetails: GetMovieDetails,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    data class MovieDetailsUiState(
        val title: String = "",
        val description: String = "",
        val imageUrl: String = "",
    )

    val movieDetail = MutableStateFlow(State(Status.LOADING, MovieDetailsUiState(), null, false))

    init {
        onInitialState()
    }

    private fun onInitialState() = launchOnMainImmediate {
        getMovieById(movieId).onSuccess {
            movieDetail.value = State.success(
                MovieDetailsUiState(
                    title = it.title!!,
                    description = it.overview!!,
                    imageUrl = it.poster_path!!
                )
            )
        }.onError {
            movieDetail.value = State.error(it.message, true)
        }
    }


    private suspend fun getMovieById(movieId: Int): Result<MovieEntity> = getMovieDetails(movieId)


    @AssistedFactory
    interface Factory {
        fun create(movieId: Int): MovieDetailsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            movieId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                assistedFactory.create(movieId) as T
        }
    }
}