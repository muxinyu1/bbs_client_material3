package com.mxy.bbs_client.program.repository

import com.mxy.bbs_client.entity.post.Post
import com.mxy.bbs_client.entity.review.Review
import com.mxy.bbs_client.entity.user.User
import com.mxy.bbs_client.entity.userinfo.UserInfo
import com.mxy.bbs_client.program.db.CacheDatabase

class CacheRepository(private val _cacheDatabase: CacheDatabase){

    fun addPost(post: Post) {
        _cacheDatabase.postDao().add(post)
    }

    fun deletePost(post: Post) {
        _cacheDatabase.postDao().delete(post)
    }

    fun updatePost(post: Post) {
        _cacheDatabase.postDao().update(post)
    }

    fun getPost(id: String): Post? {
        return _cacheDatabase.postDao().getPost(id)
    }

    fun addReview(review: Review) {
        _cacheDatabase.reviewDao().add(review)
    }

    fun deleteReview(review: Review) {
        _cacheDatabase.reviewDao().delete(review)
    }

    fun updateReview(review: Review) {
        _cacheDatabase.reviewDao().update(review)
    }

    fun getReview(id: String): Review? {
        return _cacheDatabase.reviewDao().getReview(id)
    }

    fun addUser(user: User) {
        _cacheDatabase.userDao().add(user)
    }

    fun deleteUser(user: User) {
        _cacheDatabase.userDao().delete(user)
    }

    fun updateUser(user: User) {
        _cacheDatabase.userDao().update(user)
    }

    fun getUser(username: String): User? {
        return _cacheDatabase.userDao().getUser(username)
    }

    fun addUserInfo(userInfo: UserInfo) {
        _cacheDatabase.userInfoDao().add(userInfo)
    }

    fun deleteUserInfo(userInfo: UserInfo) {
        _cacheDatabase.userInfoDao().delete(userInfo)
    }

    fun updateUserInfo(userInfo: UserInfo) {
        _cacheDatabase.userInfoDao().update(userInfo)
    }

    fun getUserInfo(username: String): UserInfo? {
        return _cacheDatabase.userInfoDao().getUserInfo(username)
    }

    fun closeDatabase() {
        if (_cacheDatabase.isOpen) {
            _cacheDatabase.close()
        }
    }

}