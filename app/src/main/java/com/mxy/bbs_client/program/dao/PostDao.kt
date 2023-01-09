package com.mxy.bbs_client.program.dao

import androidx.room.*
import com.mxy.bbs_client.entity.post.Post

@Dao
interface PostDao {

    @Insert
    fun add(post: Post)

    @Delete
    fun delete(post: Post)

    @Update
    fun update(post: Post)

    @Query("select * from Post where id = :id")
    fun getPost(id: String): Post?
}