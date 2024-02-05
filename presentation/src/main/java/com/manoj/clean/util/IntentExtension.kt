package com.manoj.clean.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Intent.canBeHandled(context: Context) = this.resolveActivity(context.packageManager) != null

fun Context.intentCanBeHandled(intent: Intent) = intent.resolveActivity(packageManager) != null

fun Context?.openGoogleMaps(query: String, placeId: String) {
    val queryEncoded = Uri.encode(query)
    val gmmIntentUri =
        Uri.parse("https://www.google.com/maps/search/?api=1&query=$queryEncoded&query_place_id=$placeId")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    this?.apply {
        if (mapIntent.canBeHandled(this)) {
            startActivity(mapIntent)
        }
    }
}