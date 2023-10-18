package com.manoj.data.mapper

import com.manoj.data.entities.MovieDbData
import com.manoj.domain.entities.MovieEntity


fun MovieEntity.toDbData() = MovieDbData(
    id = id,
    image = image,
    description = description,
    title = title,
    category = category
)
