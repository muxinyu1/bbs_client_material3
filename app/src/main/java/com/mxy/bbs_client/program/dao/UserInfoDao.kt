package com.mxy.bbs_client.program.dao

import androidx.room.*
import com.mxy.bbs_client.entity.userinfo.UserInfo

@Dao
interface UserInfoDao {

    @Insert
    fun add(userInfo: UserInfo)

    @Update
    fun update(userInfo: UserInfo)

    @Delete
    fun delete(userInfo: UserInfo)

    @Query("select * from UserInfo where username = :username")
    fun getUserInfo(username: String): UserInfo?
}