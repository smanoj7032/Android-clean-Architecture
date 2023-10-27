package com.manoj.clean.ui.moviedetails

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.manoj.clean.ui.base.BaseViewModel
import com.manoj.clean.workers.SyncWork
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.usecase.GetMovieDetails
import com.manoj.domain.util.State
import com.manoj.domain.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetails: GetMovieDetails,
    private val workManager: WorkManager,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {


    fun onInitialState(movieId: Int) = launchOnMainImmediate {
        val inputData = Data.Builder().putInt("movieId", movieId).build()
        workManager.enqueueUniqueWork(
            SyncWork::class.java.simpleName,
            ExistingWorkPolicy.KEEP,
            SyncWork.getOneTimeWorkRequest(inputData)
        )
    }

    companion object {
        val movieDetail = MutableStateFlow(State(Status.LOADING, MovieEntity(), null, false))
    }
}