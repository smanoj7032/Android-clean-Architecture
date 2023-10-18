package com.manoj.clean.ui.adapter.movie.viewholder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.manoj.clean.R
import com.manoj.clean.databinding.ItemMovieBinding
import com.manoj.clean.entities.MovieListItem
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MovieViewHolder(
    parent: ViewGroup,
    private val onMovieClick: (movieId: Int) -> Unit,
    private val imageFixedSize: Int
) : RecyclerView.ViewHolder(
    ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {

    private val binding = ItemMovieBinding.bind(itemView)

    fun bind(movie: MovieListItem.Movie) = with(binding) {
        loadImage(image, movie.imageUrl)
        root.setOnClickListener { onMovieClick(movie.id) }
    }

    fun unBind() = with(binding) {
        clearImage(image)
        root.setOnClickListener(null)
    }

    private fun loadImage(image: AppCompatImageView, url: String) = Glide.with(image)
        .asDrawable()
        .override(imageFixedSize)
        .format(DecodeFormat.PREFER_RGB_565)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .thumbnail(getThumbnailRequest(image, url))
        .load(url)
        .placeholder(R.color.light_gray)
        .error(R.drawable.bg_image)
        .into(image)

    private fun getThumbnailRequest(imageView: ImageView, url: String): RequestBuilder<Drawable> = Glide.with(imageView)
        .asDrawable()
        .override(imageFixedSize)
        .format(DecodeFormat.PREFER_RGB_565)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .sizeMultiplier(0.2F)
        .load(url)

    private fun clearImage(image: AppCompatImageView) = Glide.with(image).clear(image)
}