package com.manoj.clean.ui.common.singlexoplayer

import android.view.View

interface ControlListener {
    fun onMute(position: Int, view: View)
    fun onPlayPause(position: Int, view: View)
}