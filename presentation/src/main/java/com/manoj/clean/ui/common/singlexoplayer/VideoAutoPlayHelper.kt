package com.manoj.clean.ui.common.singlexoplayer

import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.manoj.clean.ui.favorites.FavouriteAdapter
import com.manoj.clean.ui.favorites.HorizontalPagerAdapter
import kotlinx.coroutines.delay


class VideoAutoPlayHelper(var recyclerView: RecyclerView) {
    fun getPlayer(): ExoPlayer? {
        return lastPlayerView?.getPlayer()
    }

    private var lastPlayerView: SingleExoPlayerView? = null
    private val minVisibilityPercentage =
        20

    /** When playerView will be less than 20% visible than it will stop the player*/

    private var currentPlayingVideoItemPos = -1

    /**-1 indicates nothing playing*/
    val handler = Handler(Looper.getMainLooper())

    fun startObserving() {
       // PagerSnapHelper().attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handler.postDelayed(runnable, 100)
                }else {
                    handler.removeCallbacks(runnable)
                }
            }
        })
        recyclerView.post { recyclerView.smoothScrollBy(0, 1) }
    }

    val runnable = Runnable {  onScrolled(false)
    Log.e("Check----->>>","onScrolled")
    }


    /**
     * Detects the visible view and attach/detach player from it according to visibility
     */
    fun onScrolled(forHorizontalScroll: Boolean) {
        val firstVisiblePosition: Int = findFirstVisibleItemPosition()
        val lastVisiblePosition: Int = findLastVisibleItemPosition()
        val pos = getMostVisibleItem(firstVisiblePosition, lastVisiblePosition)

        if (pos == -1) {
            /**check if current view is more than MIN_LIMIT_VISIBILITY*/
            if (currentPlayingVideoItemPos != -1) {
                val viewHolder: RecyclerView.ViewHolder? =
                    recyclerView.findViewHolderForAdapterPosition(currentPlayingVideoItemPos)

                val currentVisibility = viewHolder?.let { getVisiblePercentage(it) }
                if (currentVisibility != null) {
                    if (currentVisibility < minVisibilityPercentage) {
                        lastPlayerView?.removePlayer()
                    }
                }
                currentPlayingVideoItemPos = -1
            }
        } else {
            if (forHorizontalScroll || currentPlayingVideoItemPos != pos) {
                currentPlayingVideoItemPos = pos
                attachVideoPlayerAt(pos)
            }
        }
    }

    fun attachVideoPlayerAt(pos: Int) {
        val feedViewHolder: FavouriteAdapter.FeedViewHolder? =
            (recyclerView.findViewHolderForAdapterPosition(pos) as FavouriteAdapter.FeedViewHolder?)

        if (feedViewHolder?.recyclerViewHorizontal?.adapter is HorizontalPagerAdapter) {
            val layoutManager: LinearLayoutManager =
                feedViewHolder.recyclerViewHorizontal.layoutManager as LinearLayoutManager
            val firstVisiblePosition: Int = layoutManager.findFirstVisibleItemPosition()
            val itemViewHolder: HorizontalPagerAdapter.PagerViewHolder? =
                (feedViewHolder.recyclerViewHorizontal.findViewHolderForAdapterPosition(
                    firstVisiblePosition
                ) as HorizontalPagerAdapter.PagerViewHolder?)


            if (itemViewHolder is HorizontalPagerAdapter.VideoViewHolder) {
                /** In case its a video**/
                if (lastPlayerView == null || lastPlayerView != itemViewHolder.customPlayerView) {
                    Log.e("Position---->>", "attachVideoPlayerAt: $pos")
                    itemViewHolder.customPlayerView.startPlaying()
                    // stop last player
                    lastPlayerView?.removePlayer()
                }
                lastPlayerView = itemViewHolder.customPlayerView
            } else {
                /** in case its a image**/
                if (lastPlayerView != null) {
                    /**stop last player*/
                    lastPlayerView?.removePlayer()
                    lastPlayerView = null
                }
            }
        }
    }

    private fun getMostVisibleItem(firstVisiblePosition: Int, lastVisiblePosition: Int): Int {
        var maxPercentage = -1
        var pos = 0
        for (i in firstVisiblePosition..lastVisiblePosition) {
            val viewHolder: RecyclerView.ViewHolder? =
                recyclerView.findViewHolderForAdapterPosition(i)

            if (viewHolder != null) {
                val currentPercentage = getVisiblePercentage(viewHolder)
                if (currentPercentage > maxPercentage) {
                    maxPercentage = currentPercentage.toInt()
                    pos = i
                }
            }
        }

        if (maxPercentage == -1 || maxPercentage < minVisibilityPercentage) {
            return -1
        }
        return pos
    }

    private fun getVisiblePercentage(
        holder: RecyclerView.ViewHolder
    ): Float {
        val rectParent = Rect()
        recyclerView.getGlobalVisibleRect(rectParent)
        val location = IntArray(2)
        holder.itemView.getLocationOnScreen(location)

        val rectChild = Rect(
            location[0],
            location[1],
            location[0] + holder.itemView.width,
            location[1] + holder.itemView.height
        )

        val rectParentArea =
            ((rectChild.right - rectChild.left) * (rectChild.bottom - rectChild.top)).toFloat()
        val xOverlap = 0.coerceAtLeast(
            rectChild.right.coerceAtMost(rectParent.right) - rectChild.left.coerceAtLeast(rectParent.left)
        ).toFloat()
        val yOverlap = 0.coerceAtLeast(
            rectChild.bottom.coerceAtMost(rectParent.bottom) - rectChild.top.coerceAtLeast(
                rectParent.top
            )
        ).toFloat()
        val overlapArea = xOverlap * yOverlap

        return overlapArea / rectParentArea * 100.0f
    }


    private fun findFirstVisibleItemPosition(): Int {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            return (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        }
        return -1
    }

    private fun findLastVisibleItemPosition(): Int {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            return (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        return -1
    }

    fun pause() {
        lastPlayerView?.getPlayer()?.pause()
    }

    fun play() {
        lastPlayerView?.getPlayer()?.play()
    }
}