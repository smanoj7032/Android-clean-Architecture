package com.manoj.clean.util.geolocator

import android.content.Context
import android.util.Log
import com.manoj.clean.util.geolocator.geofencer.models.GeoFenceUpdateModule
import com.manoj.clean.util.geolocator.geofencer.models.Geofence
import com.manoj.clean.util.geolocator.misc.sendNotification


class NotificationWorker (private val context: Context): GeoFenceUpdateModule(context){
    override fun onGeofence(geofence: Geofence) {
        Log.d("","onGeofence $geofence")
        sendNotification(
            context = context,
            title = geofence.title,
            message = geofence.message
        )
    }

}