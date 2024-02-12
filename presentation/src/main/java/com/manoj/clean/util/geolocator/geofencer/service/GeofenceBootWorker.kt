package com.manoj.clean.util.geolocator.geofencer.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.manoj.clean.util.geolocator.geofencer.Geofencer

class GeofenceBootWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        return try {
            Geofencer(applicationContext).repository.reAddAll()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}