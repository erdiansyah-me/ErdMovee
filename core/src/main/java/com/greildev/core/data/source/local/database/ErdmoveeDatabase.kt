package com.greildev.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.greildev.core.data.source.local.dao.CartMovieDao
import com.greildev.core.data.source.local.dao.FavoriteMovieDao
import com.greildev.core.data.source.local.dao.NowPlayingMovieDao
import com.greildev.core.data.source.local.dao.NowPlayingMovieRemoteKeysDao
import com.greildev.core.data.source.local.dao.UserDataDao
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingRemoteKeys
import com.greildev.core.data.source.local.entities.UserDataEntity

@TypeConverters(value = [GenresNameTypeConverter::class])
@Database(
    entities = [
        NowPlayingRemoteKeys::class,
        NowPlayingMovieListEntities::class,
        FavoriteMovieListEntities::class,
        CartMovieListEntities::class,
        UserDataEntity::class
    ],
    version = 11,
    exportSchema = false
)
internal abstract class ErdmoveeDatabase : RoomDatabase() {
    internal abstract val nowPlayingMovieRemoteKeysDao: NowPlayingMovieRemoteKeysDao
    internal abstract val cartMovieDao: CartMovieDao
    internal abstract val favoriteMovieDao: FavoriteMovieDao
    internal abstract val nowPlayingMovieDao: NowPlayingMovieDao
    internal abstract val userDataDao: UserDataDao
}
