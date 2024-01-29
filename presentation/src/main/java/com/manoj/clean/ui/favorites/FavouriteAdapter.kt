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
    ListAdapter<FeedItem, FavouriteAdapter.FeedViewHolder>(DIFF_CALLBACK) {
    private val dataList: MutableList<List<FeedItem>> = ArrayList()

    companion object {
        /** Mandatory implementation inorder to use "ListAdapter" - new JetPack component" **/
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeedItem>() {
            override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
                return false
            }

        }
        const val FEED_TYPE_IMAGE = 3
        const val FEED_TYPE_VIDEO = 4
    }

    fun setData(newDataList: List<List<FeedItem>>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(dataList, newDataList))
        dataList.clear()
        dataList.addAll(newDataList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            FeedItemLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {

        /*Set ratio according to first video*/
        (holder.recyclerViewHorizontal.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
            dataList[position][0].ratio
        /* Set adapter (items are being used inside adapter, you can setup in your own way*/
        val feedAdapter = HorizontalPagerAdapter(
            context,
            position, playerHelper,
            dataList[position], activity
        )
        holder.recyclerViewHorizontal.adapter = feedAdapter
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

        /**
         * Add dots (fixed size for now)
         */
        holder.binding.dots.removeAllTabs()
        if (dataList[position].size > 1) {
            for (i in 0 until dataList[position].size) {
                holder.binding.dots.addTab(holder.binding.dots.newTab())
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class FeedViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        lateinit var recyclerViewHorizontal: RecyclerView
        lateinit var binding: FeedItemLayoutBinding

        constructor(binding: FeedItemLayoutBinding) : this(binding.root) {
            this.binding = binding
            recyclerViewHorizontal =
                binding.recyclerViewHorizontal

            /** Keep the item center aligned**/
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerViewHorizontal)
        }
    }

    private class DiffCallback<M>(
        private val oldList: List<M>, private val newList: List<M>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}



