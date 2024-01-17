package com.manoj.clean.ui.favorites

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.manoj.clean.ui.common.base.BaseViewModel
import com.manoj.clean.util.singleSharedFlow
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieDetails
import com.manoj.domain.entities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {


    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<MovieDetails> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    fun onLoadStateUpdate(loadState: CombinedLoadStates, itemCount: Int) {
        val showLoading = loadState.refresh is LoadState.Loading
        val showNoData = loadState.append.endOfPaginationReached && itemCount < 1

        _uiState.update {
            it.copy(
                showLoading = showLoading, errorMessage = showNoData.toString()
            )
        }
    }
}
