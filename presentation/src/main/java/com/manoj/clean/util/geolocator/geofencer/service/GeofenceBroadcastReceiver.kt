package com.manoj.clean.util.geolocator.geofencer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.manoj.clean.util.geolocator.geofencer.GeofenceRepository
import com.manoj.clean.util.geolocator.utils.enqueueOneTimeWorkRequest
import com.manoj.clean.util.geolocator.utils.log


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("GeoFenceUpdateWorker----->>", "GeofenceBroadcastReceiver: called")
        if (context == null) {
            return
        }

        if (intent == null) {
            return
        }

        val geofencingEvent: GeofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            Log.e("GeoFenceUpdateWorker----->>", "GeofenceBroadcastReceiver: $geofencingEvent.errorCode")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.e("GeoFenceUpdateWorker----->>", "GeofenceBroadcastReceiver: geo        fence was triggered: $geofenceTransition")
        if (geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER && geofenceTransition != Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e("GeoFenceUpdateWorker----->>", "unknow geofencing error")
            return
        }
        Log.e("GeoFenceUpdateWorker----->>", "unknow geofencing error ${geofencingEvent.triggeringGeofences?.size}")
        if ((geofencingEvent.triggeringGeofences?.size ?: 0) <= 0) return
        val repo = GeofenceRepository(context)

        log("unknow geofencing error" + repo.getAll().count())
        log("unknow geofencing error" + repo.getAll().firstOrNull()?.id)
        log("unknow geofencing error" + geofencingEvent.triggeringGeofences?.get(0)?.requestId)
        Log.e("GeoFenceUpdateWorker----->>", "unknow geofencing error\" + ${repo.getAll().count()}")
        Log.e("GeoFenceUpdateWorker----->>", "unknow geofencing error ${repo.getAll().firstOrNull()?.id}")
        Log.e("GeoFenceUpdateWorker----->>", "unknow geofencing error ${geofencingEvent.triggeringGeofences?.get(0)?.requestId}")

        val geofence = repo.get(geofencingEvent.triggeringGeofences?.get(0)?.requestId) ?: return

        Log.e("GeoFenceUpdateWorker----->>", "geofence enqeue work geofence=$geofence")
        Log.e("GeoFenceUpdateWorker----->>", "geofence enqeue work geofence=$geofence intentClassName=${geofence.intentClassName}")

        enqueueOneTimeWorkRequest(context, geofence.id)
    }
}