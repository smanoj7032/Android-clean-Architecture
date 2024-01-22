package com.manoj.clean.ui.common.singlexoplayer

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout

class FullScreenDialog(
    context: Context, videoUrl: Uri, playerView: SingleExoPlayerView,
) : Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val main = FrameLayout(context)
        setContentView(main)
        main.addView(playerView)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        playerView.setVideoUri(videoUrl)
        playerView.startPlaying()
        setOnDismissListener {
            (playerView.parent as ViewGroup).removeView(playerView)
            playerView.removePlayer()
        }
    }
}

