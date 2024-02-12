package com.manoj.clean.util.geolocator.geofencer.service

import androidx.annotation.WorkerThread
import com.google.android.gms.location.LocationResult
import com.manoj.clean.util.geolocator.geofencer.models.Geofence

internal interface GeoLocatorInterface

internal interface GeoFenceBootInterface : GeoLocatorInterface {
    @WorkerThread
    fun readAllGeoFence()

}

internal interface GeoFenceUpdateInterface : GeoLocatorInterface {
    @WorkerThread
    fun onGeofence(geofence: Geofence)

}

internal interface LocationTrackerUpdateInterface : GeoLocatorInterface {
    @WorkerThread
    fun onLocationResult(locationResult: LocationResult)

}

