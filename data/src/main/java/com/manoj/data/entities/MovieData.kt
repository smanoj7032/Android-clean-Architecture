package com.manoj.data.entities

import com.manoj.domain.entities.MovieEntity
import com.google.gson.annotations.SerializedName

data class MovieData(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
)

fun MovieData.toDomain() = MovieEntity(
    id = id,
    image = image,
    description = description,
    title = title,
    category = category
)