package com.mxy.bbs_client.program.state

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mxy.bbs_client.program.converter.Converter

@Entity(tableName = "MineScreenState")
@TypeConverters(Converter::class)
data class MineScreenState(
    @PrimaryKey val id: Int = 0,
    val login: Boolean,
    val placeholder: Int,
    val userInfoState: UserInfoState,
)
