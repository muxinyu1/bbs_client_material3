package com.mxy.bbs_client.program.state

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MineScreenState")
data class MineScreenState(
    @PrimaryKey val id: Int = 0,
    val login: Boolean,
    val username: String?,
    val placeholder: Int
)
