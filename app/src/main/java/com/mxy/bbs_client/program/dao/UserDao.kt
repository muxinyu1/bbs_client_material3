package com.mxy.bbs_client.program.dao

import androidx.room.*
import com.mxy.bbs_client.entity.user.User

@Dao
interface UserDao {
    @Insert
    fun add(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("select * from User where username = :username")
    fun getUser(username: String): User?
}