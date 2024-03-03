package com.greildev.core.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities

@Dao
interface NowPlayingMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listItemProductStore: List<NowPlayingMovieListEntities>)

    @Query("SELECT * FROM now_playing_entities ORDER BY id ASC")
    fun getAllNowPlayingMovie(): PagingSource<Int, NowPlayingMovieListEntities>

    @Query("DELETE FROM now_playing_entities")
    suspend fun deleteAll()
}