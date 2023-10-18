package com.manoj.clean.mapper

import com.manoj.clean.entities.MovieListItem
import com.manoj.domain.entities.MovieEntity


fun MovieEntity.toPresentation() = MovieListItem.Movie(
    id = id, imageUrl = image, category = category
)