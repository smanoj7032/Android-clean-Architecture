package com.manoj.clean.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.repository.BaseRepository
import com.manoj.domain.util.getResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext

const val SYNC_WORK_MAX_ATTEMPTS = 3

@HiltWorker
class SyncWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val baseRepository: BaseRepository,
    val dispatchers: DispatchersProvider,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(dispatchers.getIO()) {
        baseRepository.getMovie(1).getResult({
            Log.e("XXX", "doWork: ${it.data}")
            Result.success()
        }, {
            val lastAttempt = runAttemptCount >= SYNC_WORK_MAX_ATTEMPTS
            if (lastAttempt) {
                Log.d("XXX", "SyncWork: doWork() called -> failure")
                Result.failure()
            } else {
                Log.d("XXX", "SyncWork: doWork() called -> retry")
                Result.retry()
            }
        })
    }

    companion object {
        fun getOneTimeWorkRequest() = OneTimeWorkRequestBuilder<SyncWork>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()
    }
}