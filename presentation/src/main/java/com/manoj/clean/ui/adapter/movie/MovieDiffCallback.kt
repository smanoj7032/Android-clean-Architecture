package com.manoj.clean.ui.adapter.movie

import androidx.recyclerview.widget.DiffUtil
import com.manoj.clean.entities.MovieListItem

object MovieDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {

    override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean =
        if (oldItem is MovieListItem.Movie && newItem is MovieListItem.Movie) {
            oldItem.id == newItem.id
        } else if (oldItem is MovieListItem.Separator && newItem is MovieListItem.Separator) {
            oldItem.category == newItem.category
        } else {
            oldItem == newItem
        }

    override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean = oldItem == newItem
}