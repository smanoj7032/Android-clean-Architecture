package com.manoj.clean.ui.adapter.movie.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manoj.clean.databinding.ItemSeparatorBinding
import com.manoj.clean.entities.MovieListItem

class SeparatorViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    ItemSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {

    private val binding = ItemSeparatorBinding.bind(itemView)

    fun bind(separator: MovieListItem.Separator) {
        binding.title.text = separator.category
    }
}