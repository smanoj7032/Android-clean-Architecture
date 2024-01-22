package com.manoj.clean.ui.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manoj.clean.databinding.ImageItemSingleBinding
import com.manoj.clean.databinding.VideoItemSingleBinding
import com.manoj.clean.ui.favorites.FavouriteAdapter.Companion.FEED_TYPE_IMAGE
import com.manoj.clean.ui.favorites.FavouriteAdapter.Companion.FEED_TYPE_VIDEO

class HorizontalPagerAdapter(
    private val context: Context,
    private val parentPosition: Int,
    private val arrayList: List<FeedItem>,
) : ListAdapter<FeedItem, HorizontalPagerAdapter.PagerViewHolder>(DIFF_CALLBACK) {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return if (viewType == FEED_TYPE_VIDEO) {
            VideoViewHolder(
                VideoItemSingleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            ImageViewHolder(
                ImageItemSingleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayList[position].type == "image") {
            FEED_TYPE_IMAGE
        } else {
            FEED_TYPE_VIDEO
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "DiscouragedApi")
    override fun onBindViewHolder(pagerViewHolder: PagerViewHolder, position: Int) {
        if (pagerViewHolder is VideoViewHolder) {
            val holder: VideoViewHolder = pagerViewHolder/*Reset ViewHolder */
            (holder.videoThumbnail.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                arrayList[0].ratio

            /*Set separate ID for each player view, to prevent it being overlapped by other player's changes*/
            holder.customPlayerView.id = View.generateViewId()

            /*Set video's direct url*/
            holder.customPlayerView.setVideoUri(Uri.parse(arrayList[position].link))

            /*Set video's thumbnail locally (by drawable), you can set it by remoteUrl too*/
            Glide.with(context).load(arrayList[position].thumbnail).centerCrop()
                .into(holder.videoThumbnail)

        } else if (pagerViewHolder is ImageViewHolder) {
            val holder: ImageViewHolder = pagerViewHolder
            Glide.with(context).load(arrayList[position].thumbnail).into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    abstract class PagerViewHolder(root: View) : RecyclerView.ViewHolder(root)

    class ImageViewHolder(binding: ImageItemSingleBinding) : PagerViewHolder(binding.root) {
        val imageView =
            binding.imageView
    }

    class VideoViewHolder(binding: VideoItemSingleBinding) : PagerViewHolder(binding.root) {
        val videoThumbnail = binding.feedThumbnailView
        val customPlayerView = binding.feedPlayerView
    }

}
