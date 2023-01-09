package com.mxy.bbs_client.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey val username: String,
    val password: String?
)