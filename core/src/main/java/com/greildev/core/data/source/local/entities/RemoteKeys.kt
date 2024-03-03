package com.greildev.core.data.source.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greildev.core.utils.CoreConstant

@Entity(tableName = CoreConstant.NOW_PLAYING_REMOTE_KEY_ENTITIES)
data class NowPlayingRemoteKeys(
    @PrimaryKey val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)