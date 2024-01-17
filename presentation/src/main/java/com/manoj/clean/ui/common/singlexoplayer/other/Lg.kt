package com.manoj.clean.ui.common.singlexoplayer.other

import android.util.Log

class Lg {
    companion object {
        fun v(tag: String, log: String) {
            Log.v(tag, log)
        }

        fun printStackTrace(e: Exception) {
            e.printStackTrace()
        }

    }
}
