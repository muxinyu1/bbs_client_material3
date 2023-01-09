package com.mxy.bbs_client.program.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mxy.bbs_client.entity.post.Post
import com.mxy.bbs_client.entity.review.Review
import com.mxy.bbs_client.entity.user.User
import com.mxy.bbs_client.entity.userinfo.UserInfo
import com.mxy.bbs_client.program.dao.PostDao
import com.mxy.bbs_client.program.dao.ReviewDao
import com.mxy.bbs_client.program.dao.UserDao
import com.mxy.bbs_client.program.dao.UserInfoDao

@Database(entities = [Post::class, Review::class, User::class, UserInfo::class], version = 2)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao
    abstract fun userInfoDao(): UserInfoDao
}