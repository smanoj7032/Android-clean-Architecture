package com.manoj.clean.ui.popularmovies

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.clean.util.singleSharedFlow
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.PopularMovieEntity
import com.manoj.domain.usecase.PopularMovies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    popularMovies: PopularMovies,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {
    data class FeedUiState(
        val showLoading: Boolean = true, val errorMessage: String? = null
    )

    sealed class NavigationState { data class MovieDetails(val movieId: Int?) : NavigationState() }

    fun onMovieClicked(movieId: Int?) =
        _navigationState.tryEmit(NavigationState.MovieDetails(movieId))

    private val _uiState: MutableStateFlow<FeedUiState> = MutableStateFlow(
        FeedUiState()
    )
    val uiState = _uiState.asStateFlow()
    var movies: Flow<PagingData<PopularMovieEntity>> = popularMovies(10).cachedIn(viewModelScope)
    private val _navigationState: MutableSharedFlow<NavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()
    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }
        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
    }

    companion object
}