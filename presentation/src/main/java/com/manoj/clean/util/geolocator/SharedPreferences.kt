package com.manoj.clean.util.geolocator

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.manoj.clean.App


val sharedPreferences: SharedPreferences?
    get() {
        return PreferenceManager
            .getDefaultSharedPreferences(App.applicationContext())
    }