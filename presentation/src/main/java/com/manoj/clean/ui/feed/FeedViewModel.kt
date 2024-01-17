package com.manoj.clean.ui.feed

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.clean.ui.common.base.BaseViewModel
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.UiState
import com.manoj.domain.usecase.GetMoviesWithSeparators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    getMoviesWithSeparators: GetMoviesWithSeparators,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {


    val movies: Flow<PagingData<MovieEntity>> = getMoviesWithSeparators.movies(
        pageSize = 10
    ).cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()


    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
    }
}
