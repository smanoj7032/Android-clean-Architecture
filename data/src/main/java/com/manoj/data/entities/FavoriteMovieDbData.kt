package com.manoj.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieDbData(
    @PrimaryKey val movieId: Int
)