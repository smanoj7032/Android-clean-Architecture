package com.manoj.clean.ui.popularmovies

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.domain.usecase.GetMoviesWithSeparators
import com.manoj.data.util.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    getMoviesWithSeparators: GetMoviesWithSeparators, dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {
    data class FeedUiState(
        val showLoading: Boolean = true, val errorMessage: String? = null
    )

    private val _uiState: MutableStateFlow<FeedUiState> = MutableStateFlow(
        FeedUiState()
    )
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