package com.greildev.core.data.source.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greildev.core.utils.CoreConstant

@Entity(tableName = CoreConstant.USER_DATA_ENTITY)
data class UserDataEntity(
    @PrimaryKey
    val uid: String,
    val displayName: String = "",
    val photoUrl: String = "",
    val email: String = "",
    val phoneNumber: String = ""
)
