package com.manoj.clean.ui.moviedetails

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.usecase.AddMovieToFavorite
import com.manoj.domain.usecase.CheckFavoriteStatus
import com.manoj.domain.usecase.GetMovieDetails
import com.manoj.domain.usecase.RemoveMovieFromFavorite
import com.manoj.domain.util.Result
import com.manoj.domain.util.State
import com.manoj.domain.util.Status
import com.manoj.domain.util.getResult
import com.manoj.domain.util.onError
import com.manoj.domain.util.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieDetailsViewModel @AssistedInject constructor(
    @Assisted private var movieId: Int,
    private val getMovieDetails: GetMovieDetails,
    private val checkFavoriteStatus: CheckFavoriteStatus,
    private val addMovieToFavorite: AddMovieToFavorite,
    private val removeMovieFromFavorite: RemoveMovieFromFavorite,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    data class MovieDetailsUiState(
        val title: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val isFavorite: Boolean = false,
    )

    val movieDetail = MutableStateFlow(State(Status.LOADING, MovieDetailsUiState(), null, false))

    init {
        onInitialState()
    }

    /** @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE), it's a signal that the method is not
     * part of the public API, and it's intended for internal use within the class. In testing, you should
     * focus on testing the public API and not directly access or test such "private" methods or fields.
     */

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun onInitialState() = launchOnMainImmediate {
        val isFavorite = async {
            checkFavoriteStatus(movieId).getResult({ favoriteResult -> favoriteResult.data },
                { false })
        }
        getMovieById(movieId).onSuccess {
            movieDetail.value = State.success(
                MovieDetailsUiState(
                    title = it.title,
                    description = it.description,
                    imageUrl = it.image,
                    isFavorite = isFavorite.await()
                )
            )
        }.onError {
            movieDetail.value = State.error(it.message, true)
        }
    }

    fun onFavoriteClicked() = launchOnMainImmediate {
        checkFavoriteStatus(movieId).onSuccess { isFavorite ->
            if (isFavorite) removeMovieFromFavorite(movieId) else addMovieToFavorite(movieId)
            movieDetail.update { currentState ->
                State.success(currentState.data?.copy(  isFavorite = !isFavorite))
            }
        }
    }

    private suspend fun getMovieById(movieId: Int): Result<MovieEntity> = getMovieDetails(movieId)

    private suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean> =
        checkFavoriteStatus.invoke(movieId)

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