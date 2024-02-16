package com.manoj.clean.util.geolocator.geofencer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence.Builder
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.manoj.clean.R
import com.manoj.clean.util.geolocator.geofencer.models.Geofence
import com.manoj.clean.util.geolocator.geofencer.service.GeofenceBroadcastReceiver
import com.manoj.clean.util.geolocator.utils.fromJson
import com.manoj.clean.util.geolocator.utils.getRes
import com.manoj.clean.util.geolocator.utils.loge
import com.manoj.clean.util.geolocator.utils.sharedPreference
import com.manoj.clean.util.geolocator.utils.toJson

class GeofenceRepository(private val context: Context) {

    private val geofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent
        get() {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            return PendingIntent.getBroadcast(
                context,
                Geofencer.REQUEST_CODE,
                intent,
                flags
            )
        }

    var geofenceString by context.sharedPreference(Geofencer.PREFS_NAME, "")

    @SuppressLint("MissingPermission")
    fun add(
        geofence: Geofence,
        success: () -> Unit
    ) {

        val androidGeofence = buildGeofence(geofence)
        geofencingClient
            .addGeofences(buildGeofencingRequest(androidGeofence), geofencePendingIntent)
            .addOnSuccessListener {
                geofenceString = (getAll() + geofence).toJson()
                success()
            }
            .addOnFailureListener {
                loge(it.message)
                Log.e("GeoFenceUpdateWorker----->>", "add: $it")
            }
    }

    fun remove(
        geofence: Geofence,
        success: () -> Unit
    ) {
        val list = getAll() - geofence
        geofenceString = list.toJson()
        success()
    }

    fun removeAll(
        success: () -> Unit
    ) {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                geofenceString = ""
                success()
            }
            addOnFailureListener {
                loge(it.message)
            }
        }
    }

    fun get(requestId: String?) = getAll().firstOrNull { it.id == requestId }

    fun getAll(): List<Geofence> {
        if (geofenceString == "") {
            return listOf()
        }
        val arrayOfReminders = geofenceString.fromJson<Array<Geofence>>()
        return arrayOfReminders.toList()
    }

    fun reAddAll() {
        val geofences = getAll()
        removeAll {
            geofences.forEach {
                add(it) {}
            }
        }
    }

    private fun buildGeofence(geofence: Geofence): com.google.android.gms.location.Geofence {
        return Builder()
            .setRequestId(geofence.id)
          /*  .setLoiteringDelay(context.resources.getInteger(R.integer.loitering_delay))*/
            .setNotificationResponsiveness(context.resources.getInteger(R.integer.notification_responsiveness))
            .setExpirationDuration(context.getRes(R.integer.expiration_duration))
            .setCircularRegion(
                geofence.latitude,
                geofence.longitude,
                geofence.radius.toFloat()
            )
            .setTransitionTypes(geofence.transitionType)
            .setExpirationDuration(NEVER_EXPIRE)
            .build()
    }

    private fun buildGeofencingRequest(geofence: com.google.android.gms.location.Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER)
            .addGeofences(listOf(geofence))
            .build()
    }

}