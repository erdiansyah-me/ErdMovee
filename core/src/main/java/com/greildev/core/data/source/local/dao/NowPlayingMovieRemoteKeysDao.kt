package com.greildev.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greildev.core.data.source.local.entities.NowPlayingRemoteKeys

@Dao
interface NowPlayingMovieRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<NowPlayingRemoteKeys>)

    @Query("SELECT * FROM now_playing_remote_key_entities WHERE now_playing_remote_key_entities.id = :id")
    suspend fun getRemoteKeysId(id: Int): NowPlayingRemoteKeys?

    @Query("DELETE FROM now_playing_remote_key_entities")
    suspend fun deleteRemoteKeys()
}