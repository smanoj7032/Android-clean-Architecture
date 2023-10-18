package com.manoj.data.db.movies

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manoj.data.db.favoritemovies.FavoriteMovieDao
import com.manoj.data.entities.FavoriteMovieDbData
import com.manoj.data.entities.MovieDbData
import com.manoj.data.entities.MovieRemoteKeyDbData

@Database(
    entities = [MovieDbData::class, FavoriteMovieDbData::class, MovieRemoteKeyDbData::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieRemoteKeysDao(): MovieRemoteKeyDao
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}