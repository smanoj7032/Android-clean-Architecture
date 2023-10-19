package com.manoj.clean.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.clean.util.singleSharedFlow
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.usecase.SearchMovies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMovies,
    private val savedStateHandle: SavedStateHandle,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    data class SearchUiState(
        val showDefaultState: Boolean = true,
        val showLoading: Boolean = false,
        val showNoMoviesFound: Boolean = false,
        val errorMessage: String? = null
    )

    sealed class NavigationState {
        data class MovieDetails(val movieId: Int) : NavigationState()
    }


    var movies: Flow<PagingData<MovieEntity>> = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
        .onEach { query ->
            _uiState.value = if (query.isNotEmpty()) SearchUiState(
                showDefaultState = false,
                showLoading = true
            ) else SearchUiState()
        }
        .debounce(500)
        .filter { it.isNotEmpty() }
        .flatMapLatest { query ->
            searchMovies(query, 30)
        }.cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<NavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    fun onSearch(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    fun onMovieClicked(movieId: Int) =
        _navigationState.tryEmit(NavigationState.MovieDetails(movieId))

    fun getSearchQuery(): CharSequence? = savedStateHandle.get<String>(KEY_SEARCH_QUERY)

    fun onLoadStateUpdate(loadState: CombinedLoadStates, itemCount: Int) {
        val showLoading = loadState.refresh is LoadState.Loading
        val showNoData = loadState.append.endOfPaginationReached && itemCount < 1

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update {
            it.copy(
                showLoading = showLoading,
                showNoMoviesFound = showNoData,
                errorMessage = error
            )
        }
    }

    companion object {
        const val KEY_SEARCH_QUERY = "search_query"
    }
}
