package com.manoj.clean.ui.common.singlexoplayer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import com.manoj.clean.App
import com.manoj.clean.R


class SingleExoPlayerView @OptIn(UnstableApi::class) @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(
    context!!, attrs, defStyleAttr
) {
    companion object {
        var isMuted: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
        var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    }

    private var videoSurfaceView: View?
    private var player: ExoPlayer? = null
    private var isTouching = false
    private var lastPos: Long? = 0
    private var videoUri: Uri? = null
    private var durationTextView: TextView? = null
    private var progress: ProgressBar? = null
    private var timeBar: DefaultTimeBar? = null
    private val updateHandler = Handler(Looper.getMainLooper())


    private val updateRunnable = object : Runnable {
        override fun run() {
            updateDurationText(player?.currentPosition ?: 0)
            updateHandler.postDelayed(this, 1000) // Repeat every 1 second
        }
    }

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    fun getPlayer(): ExoPlayer? {
        return player
    }


    /**
     * Set the [Player] to use.
     *

     * To transition a [Player] from targeting one view to another, it's recommended to use
     * [.switchTargetView] rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view *before*
     * calling `setPlayer(null)` to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The [Player] to use, or `null` to detach the current player. Only
     * players which are accessed on the main thread are supported (`player.getApplicationLooper() == Looper.getMainLooper()`).
     */
    private fun setPlayer(player: ExoPlayer?) {
        if (this.player === player) {
            return
        }
        val oldPlayer = this.player

        oldPlayer?.clearVideoSurfaceView(videoSurfaceView as SurfaceView?)
        this.player = player
        player?.setVideoSurfaceView(videoSurfaceView as SurfaceView?)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        videoSurfaceView?.visibility = visibility
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
            videoSurfaceView = null

        } else {
            val playerLayoutId = R.layout.exo_simple_player_view
            LayoutInflater.from(context).inflate(playerLayoutId, this)
            descendantFocusability = FOCUS_AFTER_DESCENDANTS

            // Content frame.
            videoSurfaceView = findViewById(R.id.surface_view)
            durationTextView = findViewById(R.id.text_view_duration)
            progress = findViewById(R.id.progress)
            timeBar = findViewById(R.id.time_bar)
            timeBar?.addListener(object : TimeBar.OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, position: Long) {}

                override fun onScrubMove(timeBar: TimeBar, position: Long) {}

                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                    if (!canceled) {
                        player?.seekTo(position)
                        updateDurationText(position)
                    }
                }
            })
            initPlayer()
        }
    }

    private fun initPlayer() {
        reset()

        /*Setup player + Adding Cache Directory*/
        val player = ExoPlayer.Builder(context).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.volume = (1f).takeIf { isMuted.value == false } ?: (0f)
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                print("playbackState== $playbackState")
                if (playbackState == Player.STATE_READY) {
                    alpha = 1f
                }
                progress?.isVisible = playbackState == Player.STATE_BUFFERING

            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                SingleExoPlayerView.isPlaying.value = isPlaying

            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                if (timeline.isEmpty) return
                val durationMs = timeline.getPeriod(0, Timeline.Period()).durationMs
                Log.e("Duration--->>", "onTimelineChanged: $durationMs")
                if (player.playWhenReady) {
                    updateHandler.post(updateRunnable)
                }
            }
        })
        setPlayer(player)
    }

    @OptIn(UnstableApi::class)
    private fun updateDurationText(durationMs: Long) {
        val durationFormatted = formatDuration(durationMs)
        val videoDuration = player?.duration ?: 0

        if (videoDuration > 0) {
            val formattedVideoDuration = formatDuration(videoDuration)
            durationTextView?.text = "${durationFormatted}  :  $formattedVideoDuration"

            timeBar?.setPosition(durationMs)
            timeBar?.setDuration(videoDuration)
        } else {
            durationTextView?.text = durationFormatted
            timeBar?.setPosition(0)
            timeBar?.setDuration(0)
        }
        durationTextView?.visibility = View.VISIBLE
    }


    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /**
     * This will resuse the player and will play new URI we have provided
     */
    @OptIn(UnstableApi::class)
    fun startPlaying() {
        if (videoUri == null) return
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

    /**
     * This will stop the player, but stopping the player shows blackscreen
     * so to cover that we set alpha to 0 of player
     * and lastFrame of player using imageView over player to make it look like paused player
     *
     * (If we will not stop the player, only pause , then it can cause memory issue due to overload of player
     * and paused player can not be payed with new URL, after stopping the player we can reuse that with new URL
     *
     */
    fun removePlayer() {
        getPlayer()?.playWhenReady = false
        lastPos = getPlayer()?.currentPosition
        updateHandler.removeCallbacks(updateRunnable)

        reset()
        getPlayer()?.stop()
    }

    fun reset() {
        // This will prevent surface view to show black screen,
        // and we will make it visible when it will be loaded
        alpha = 0f
    }

    fun setVideoUri(uri: Uri?) {
        this.videoUri = uri
    }
}