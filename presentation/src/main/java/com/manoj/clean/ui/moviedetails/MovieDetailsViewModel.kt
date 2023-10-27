package com.manoj.clean.ui.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.usecase.GetMovieDetails
import com.manoj.domain.util.State
import com.manoj.domain.util.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow

class MovieDetailsViewModel @AssistedInject constructor(
    @Assisted private var movieId: Int,
    private val getMovieDetails: GetMovieDetails,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    val movieDetail = MutableStateFlow(State(Status.LOADING, MovieEntity(), null, false))

    init {
        onInitialState()
    }

    private fun onInitialState() = launchOnMainImmediate {
        val result = getMovieDetails(movieId)
        when (result.status) {
            Status.LOADING -> movieDetail.value = State.loading()
            Status.ERROR -> movieDetail.value = State.error(result.message, true)
            else -> movieDetail.value = State.success(result.data)
        }
    }


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