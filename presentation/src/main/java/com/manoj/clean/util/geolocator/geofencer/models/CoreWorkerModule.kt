package com.manoj.clean.util.geolocator.geofencer.models

import android.content.Context
import com.manoj.clean.util.geolocator.geofencer.service.GeoFenceBootInterface
import com.manoj.clean.util.geolocator.geofencer.service.GeoFenceUpdateInterface
import com.manoj.clean.util.geolocator.geofencer.service.GeoLocatorInterface
import com.manoj.clean.util.geolocator.geofencer.service.LocationTrackerUpdateInterface

abstract class CoreWorkerModule(context: Context) : GeoLocatorInterface
abstract class GeoFenceUpdateModule(context: Context) : GeoFenceUpdateInterface
abstract class GeoFenceBootModule(context: Context) : GeoFenceBootInterface
abstract class LocationTrackerUpdateModule(context: Context) : LocationTrackerUpdateInterface
