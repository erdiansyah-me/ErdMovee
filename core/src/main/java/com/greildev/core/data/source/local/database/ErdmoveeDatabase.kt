package com.greildev.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.greildev.core.data.source.local.dao.CartMovieDao
import com.greildev.core.data.source.local.dao.FavoriteMovieDao
import com.greildev.core.data.source.local.dao.NowPlayingMovieDao
import com.greildev.core.data.source.local.dao.NowPlayingMovieRemoteKeysDao
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingRemoteKeys

@TypeConverters(value = [GenresNameTypeConverter::class])
@Database(
    entities = [
        NowPlayingRemoteKeys::class,
        NowPlayingMovieListEntities::class,
        FavoriteMovieListEntities::class,
        CartMovieListEntities::class
    ],
    version = 8,
    exportSchema = false
)
abstract class ErdmoveeDatabase : RoomDatabase() {
    abstract fun nowPlayingMovieRemoteKeysDao(): NowPlayingMovieRemoteKeysDao
    abstract fun cartMovieDao(): CartMovieDao
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun nowPlayingMovieDao(): NowPlayingMovieDao
}