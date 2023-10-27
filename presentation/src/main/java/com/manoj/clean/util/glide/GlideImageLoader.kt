package com.manoj.clean.util.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.manoj.clean.R
import com.manoj.clean.util.glide.ProgressAppGlideModule

class GlideImageLoader(
    private val mImageView: ImageView, private val mProgressBar: ProgressBar?
) {

    fun load(url: String?) {
        if (url == null) return;
        onConnecting()
        // Set Listener & start
        ProgressAppGlideModule.expect(url, object : ProgressAppGlideModule.UIonProgressListener {
            override fun onProgress(bytesRead: Long, expectedLength: Long) {
                if (mProgressBar != null) {
                    mProgressBar.progress = (100 * bytesRead / expectedLength).toInt()
                }
            }

            override fun getGranualityPercentage(): Float {
                return 1.0f
            }
        })

        // Get Image
        Glide.with(mImageView.context).load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Skip caching
            .skipMemoryCache(true) // Skip caching in memory as well
            .transition(withCrossFade()).placeholder(R.drawable.bg_image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    ProgressAppGlideModule.forget(url)
                    onFinished()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    ProgressAppGlideModule.forget(url)
                    onFinished()
                    return false
                }
            }).into(mImageView)
    }

    private fun onConnecting() {
        if (mProgressBar != null) mProgressBar.visibility = View.VISIBLE
    }

    private fun onFinished() {
        if (mProgressBar != null && mImageView != null) {
            mProgressBar.visibility = View.GONE
            mImageView.visibility = View.VISIBLE
        }
    }
}
