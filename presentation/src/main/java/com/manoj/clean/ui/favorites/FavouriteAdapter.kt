package com.manoj.clean.ui.favorites

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.manoj.clean.databinding.FeedItemLayoutBinding
import com.manoj.clean.ui.common.singlexoplayer.VideoAutoPlayHelper


class FavouriteAdapter(
    private val context: Context,
    private val activity: Activity,
    private val scrollListener: RecyclerView.OnScrollListener,
    private val playerHelper: VideoAutoPlayHelper
) :
    ListAdapter<List<FeedItem>, FavouriteAdapter.FeedViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<List<FeedItem>>() {
            override fun areItemsTheSame(
                oldItem: List<FeedItem>,
                newItem: List<FeedItem>
            ): Boolean {
                return oldItem.size == newItem.size && oldItem.containsAll(newItem)
            }

            override fun areContentsTheSame(
                oldItem: List<FeedItem>,
                newItem: List<FeedItem>
            ): Boolean {
                return true
            }
        }
        const val
                FEED_TYPE_IMAGE = 3
        const val
                FEED_TYPE_VIDEO = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding =
            FeedItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val dataList = getItem(position)

        (holder.recyclerViewHorizontal.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
            dataList[0].ratio

        val feedAdapter =
            HorizontalPagerAdapter(context, position, playerHelper, dataList, activity)
        holder.recyclerViewHorizontal.adapter = feedAdapter

        val layoutManager = holder.recyclerViewHorizontal.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(0, 0)
        holder.recyclerViewHorizontal.clearOnScrollListeners()
        holder.recyclerViewHorizontal.addOnScrollListener(scrollListener)
        holder.recyclerViewHorizontal.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val itemPosition: Int =
                    (holder.recyclerViewHorizontal.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                try {
                    holder.binding.dots.getTabAt(itemPosition)?.select()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        holder.binding.dots.removeAllTabs()

        if (dataList.size > 1) {
            for (i in dataList.indices) {
                holder.binding.dots.addTab(holder.binding.dots.newTab())
            }
        }
    }

    inner class FeedViewHolder(val binding: FeedItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val recyclerViewHorizontal = binding.recyclerViewHorizontal

        init {
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerViewHorizontal)
        }
    }
}




