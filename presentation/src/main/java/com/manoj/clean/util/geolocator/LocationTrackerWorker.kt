package com.manoj.clean.util.geolocator

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.android.gms.location.LocationResult
import com.manoj.clean.util.geolocator.geofencer.models.LocationTrackerUpdateModule
import com.manoj.clean.util.geolocator.misc.toJson

class LocationTrackerWorker(context: Context) : LocationTrackerUpdateModule(context) {

    override fun onLocationResult(locationResult: LocationResult) {

        Log.v("","locationResult=$locationResult")

        sharedPreferences?.edit {
            putString(USER_LOCATION_KEY, locationResult.toJson())
        }
    }

    companion object {
        const val USER_LOCATION_KEY = "user_location"
    }
}