package com.mxy.bbs_client.program.dao

import androidx.room.*
import com.mxy.bbs_client.entity.review.Review

@Dao
interface ReviewDao {
    @Insert
    fun add(review: Review)

    @Delete
    fun delete(review: Review)

    @Update
    fun update(review: Review)

    @Query("select * from Review where id = :id")
    fun getReview(id: String): Review?
}