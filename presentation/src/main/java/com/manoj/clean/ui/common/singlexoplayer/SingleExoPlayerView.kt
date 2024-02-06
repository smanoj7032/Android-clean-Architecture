package com.manoj.clean.ui.common.singlexoplayer

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.manoj.clean.App
import com.manoj.clean.R

/**
USE THIS PLAYER VIEW YOU HAVE TO MAKE THESE CLASSES AND LAYOUT
1. SingleExoPlayerView
2. VideoAutoPlayHelper
3. CustomViewBinding
4. exo_player_view
5. exo_simple_player_view
6. custom_controls
 */

class SingleExoPlayerView @OptIn(UnstableApi::class) @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(
    context!!, attrs, defStyleAttr
) {
    companion object {
        var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    }

    private var player: ExoPlayer? = null
    private var isTouching = false
    private var lastPos: Long? = 0
    private var videoUri: Uri? = null
    private var playerView: PlayerView? = null
    private var thumbnail: ImageView? = null
    private var relLayout: RelativeLayout? = null
    private var ivFullScreen: ImageView? = null
    private var rootLayout: ConstraintLayout? = null
    private lateinit var mFullScreenDialog: Dialog
    private var isFullScreen = false

    interface OnFullScreenListener {
        fun onFullScreenExit()
        fun onFullScreenOpen()
        fun onThumbnailClick()
    }

    private var playerListener: OnFullScreenListener? = null

    fun getPlayer(): ExoPlayer? {
        return player
    }

    fun setPlayerListener(listener: OnFullScreenListener) {
        playerListener = listener
    }

    @OptIn(UnstableApi::class)
    private fun setPlayer(player: ExoPlayer?) {
        if (this.player === player) {
            return
        }
        val oldPlayer = this.player

        oldPlayer?.clearVideoSurfaceView(playerView?.videoSurfaceView as SurfaceView?)
        this.player = player
        player?.setVideoSurfaceView(playerView?.videoSurfaceView as SurfaceView?)
        playerView?.player = player
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        playerView?.visibility = visibility
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (player != null && player!!.isPlayingAd) {
            return super.dispatchKeyEvent(event)
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (player == null) {
            false
        } else when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                true
            }

            MotionEvent.ACTION_UP -> {
                if (isTouching) {
                    isTouching = false
                    performClick()
                    return true
                }
                false
            }

            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    override fun onTrackballEvent(ev: MotionEvent): Boolean {
        return false
    }


    init {
        if (isInEditMode) {
            playerView = null

        } else {
            val playerLayoutId = R.layout.exo_simple_player_view
            LayoutInflater.from(context).inflate(playerLayoutId, this)
            descendantFocusability = FOCUS_AFTER_DESCENDANTS
            initViews()
            initFullscreenDialog()
            initPlayer()
        }
    }

    private fun initPlayer() {
        reset()
        val player = ExoPlayer.Builder(context).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                print("playbackState== $playbackState")
                if (playbackState == Player.STATE_READY) {
                    playerView?.alpha = 1f
                }
                if (playbackState == Player.STATE_BUFFERING) {
                    rootLayout?.findViewById<ProgressBar>(R.id.progress_bar)?.isVisible =
                        true
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) rootLayout?.findViewById<ProgressBar>(R.id.progress_bar)?.isVisible =
                    false
                SingleExoPlayerView.isPlaying.value = isPlaying
            }
        })
        setPlayer(player)
    }


    @OptIn(UnstableApi::class)
    fun startPlaying() {
        if (videoUri == null) return
        relLayout?.isVisible = false
        val mediaItem = MediaItem.fromUri(videoUri!!)
        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory().setCache(App.cache).setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory().setUserAgent("ExoPlayer")
            )

        val mediaSource =
            ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)
        player?.seekTo(lastPos!!)
        player?.prepare()
        player?.play()
    }

    fun removePlayer() {
        getPlayer()?.playWhenReady = false
        lastPos = getPlayer()?.currentPosition
        reset()
        getPlayer()?.stop()
    }

    fun setVideoUri(uri: Uri?, thumbNailUri: String) {
        this.videoUri = uri
        thumbnail?.let { Glide.with(context).load(thumbNailUri).into(it) }
    }


    private fun enterFullScreen() {
        playerListener?.onFullScreenOpen()
        detachVideoSurfaceView()
        attachVideoSurfaceViewToDialog()
        isFullScreen = true
        mFullScreenDialog.show()
    }

    private fun exitFullScreen() {
        playerListener?.onFullScreenExit()
        detachVideoSurfaceViewFromDialog()
        attachVideoSurfaceViewToParent()
        isFullScreen = false
        mFullScreenDialog.dismiss()
        ivFullScreen?.isSelected = false
    }

    private fun detachVideoSurfaceView() {
        (rootLayout?.parent as? ViewGroup)?.removeView(rootLayout)
    }

    private fun detachVideoSurfaceViewFromDialog() {
        (rootLayout?.parent as? ViewGroup)?.removeView(rootLayout)
    }

    private fun attachVideoSurfaceViewToParent() {
        (findViewById<View>(R.id.main_media_frame) as FrameLayout).addView(rootLayout)
    }

    private fun initFullscreenDialog() {
        mFullScreenDialog = object : Dialog(
            context, android.R.style.Theme_Black_NoTitleBar_Fullscreen
        ) {
            init {
                setOnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                        if (isFullScreen) {
                            exitFullScreen()
                        } else {
                            dismiss()
                        }
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
            }

        }
        mFullScreenDialog.window?.attributes?.windowAnimations = R.style.dialog_grow_in
        mFullScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }


    @OptIn(UnstableApi::class)
    private fun attachVideoSurfaceViewToDialog() {
        val videoFormat = player?.videoFormat
        val videoWidth = videoFormat?.width ?: 0
        val videoHeight = videoFormat?.height ?: 0

        if (playerView != null) {
            val layoutParams = playerView?.layoutParams as? LayoutParams

            if (layoutParams != null) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

                if (videoWidth > 0 && videoHeight > 0) {
                    val aspectRatio = videoHeight.toDouble() / videoWidth.toDouble()

                    rootLayout?.post {
                        if (rootLayout != null) {
                            val screenWidth = rootLayout?.width ?: 0
                            val targetHeight = (screenWidth * aspectRatio).toInt()

                            layoutParams.width = screenWidth
                            layoutParams.height = targetHeight

                            playerView?.videoSurfaceView?.layoutParams = layoutParams
                        }
                    }
                }
            }
        }
        mFullScreenDialog.setContentView(
            rootLayout!!, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun initViews() {
        playerView = findViewById(R.id.player_view)
        thumbnail = findViewById(R.id.feedThumbnailView)
        relLayout = findViewById(R.id.relLay)
        rootLayout = findViewById(R.id.root_layout)
        ivFullScreen = findViewById(R.id.iv_fullscreen)
        val play = playerView?.findViewById<ImageView>(R.id.exo_play)

        thumbnail?.setOnClickListener { playerListener?.onThumbnailClick() }
        val mute = playerView?.findViewById<ImageView>(R.id.muteIcon)

        mute?.setOnClickListener {
            if ((getPlayer()?.volume == 0f)) {
                getPlayer()?.volume = 1.0F
                mute.isSelected = (false)
            } else {
                getPlayer()?.volume = 0F
                mute.isSelected = (true)
            }
        }
        play?.setOnClickListener {
            if (isPlaying.value == false) {
                play.isSelected = false
                getPlayer()?.play()
            } else {
                play.isSelected = true
                getPlayer()?.pause()
            }
        }
        ivFullScreen?.setOnClickListener {
            if (!isFullScreen) {
                enterFullScreen()
                ivFullScreen?.isSelected = true
            } else {
                exitFullScreen()
            }
        }
    }

    private fun reset() {
        playerView?.alpha = 0f
        relLayout?.isVisible = true
    }
}