package com.mxy.bbs_client.utility

import android.util.Log
import com.google.gson.Gson
import com.mxy.bbs_client.entity.action.ActionRequest
import com.mxy.bbs_client.entity.action.ActionResponse
import com.mxy.bbs_client.entity.post.PostResponse
import com.mxy.bbs_client.entity.post.PostResponseFailedReason
import com.mxy.bbs_client.entity.postlist.PostListResponse
import com.mxy.bbs_client.entity.review.ReviewResponse
import com.mxy.bbs_client.entity.review.ReviewResponseFailedReason
import com.mxy.bbs_client.entity.user.User
import com.mxy.bbs_client.entity.user.UserResponse
import com.mxy.bbs_client.entity.user.UserResponseFailedReason
import com.mxy.bbs_client.entity.userinfo.UserInfoResponse
import com.mxy.bbs_client.serverinfo.jsonMediaType
import com.mxy.bbs_client.serverinfo.serverUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

object Client {
    private val client by lazy {
        OkHttpClient.Builder().connectTimeout(6000, TimeUnit.SECONDS)
            .readTimeout(6000, TimeUnit.SECONDS)
            .writeTimeout(6000, TimeUnit.SECONDS)
            .build()
    }

//    private lateinit var cacheDatabase: CacheDatabase

//    private lateinit var cacheRepository: CacheRepository

//    fun createCacheDatabase(app: Application) {
//        cacheDatabase = Room.databaseBuilder(app, CacheDatabase::class.java, "cache_data.db")
//            .fallbackToDestructiveMigration().build()
//        cacheRepository = CacheRepository(cacheDatabase)
//    }

//    fun closeDatabase() {
//        cacheRepository.closeDatabase()
//    }

    private val gson by lazy {
        Gson()
    }
    fun getUser(username: String?): UserResponse {
        if (username == null) {
            return UserResponse(false, UserResponseFailedReason.USERNAME_DOES_NOT_EXIST, null)
        }
        val requestBody =
            gson.toJson(User(username, null)).toRequestBody(jsonMediaType.toMediaType())
        val request = Request.Builder().url("$serverUrl/user/query").post(requestBody).build()
        val userResponse = client.newCall(request).execute()
        return gson.fromJson(userResponse.body?.string(), UserResponse::class.java)
    }

    fun getPostList(): PostListResponse {
        val request = Request.Builder().url("$serverUrl/postList/get").build()
        val postListResponse = client.newCall(request).execute()
        return gson.fromJson(postListResponse.body?.string(), PostListResponse::class.java)
    }

    fun getPost(postId: String?): PostResponse {
        if (postId == null) {
            return PostResponse(false, PostResponseFailedReason.POST_DOES_NOT_EXIST, null)
        }
//        val localPost = cacheRepository.getPost(postId)
//        if (localPost != null) {
//            Log.d("缓存", "本地存在${gson.toJson(localPost)}")
//            return PostResponse(true, null, localPost)
//        }
        val requestBody = "".toRequestBody(null)
        //这些form-data对应PostRequest数据类
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("id", postId)
            .addFormDataPart("owner", "")
            .addFormDataPart("title", "")
            .addFormDataPart("images", "", requestBody)
            .build()
        val request = Request.Builder().url("$serverUrl/post/query").post(multipartBody).build()
        val postResponse = client.newCall(request).execute()
        val remotePostResponse =
            gson.fromJson(postResponse.body?.string(), PostResponse::class.java)
//        if (remotePostResponse.success!!) {
//            try {
//                cacheRepository.addPost(remotePostResponse.post!!)
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remotePostResponse
    }

    fun getUserInfo(username: String?): UserInfoResponse {
        if (username == null) {
            return UserInfoResponse(false, UserResponseFailedReason.USERNAME_DOES_NOT_EXIST, null)
        }
//        val localUserInfo = cacheRepository.getUserInfo(username)
//        if (localUserInfo != null) {
//            Log.d("缓存", "本地存在${gson.toJson(localUserInfo)}")
//            return UserInfoResponse(true, null, localUserInfo)
//        }
        val requestBody = "".toRequestBody(null)
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("nickname", "")
            .addFormDataPart("personalSign", "")
            .addFormDataPart("avatar", "", requestBody)
            .build()
        val request = Request.Builder().url("$serverUrl/userInfo/query").post(multipartBody).build()
        val userInfoResponse = client.newCall(request).execute()
        val remoteUserInfoResponse =
            gson.fromJson(userInfoResponse.body?.string(), UserInfoResponse::class.java)
//        if (remoteUserInfoResponse.success!!) {
//            try {
//                cacheRepository.addUserInfo(remoteUserInfoResponse.userInfo!!)
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remoteUserInfoResponse
    }

    fun getReview(reviewId: String?): ReviewResponse {
        if (reviewId == null) {
            return ReviewResponse(false, ReviewResponseFailedReason.REVIEW_DOES_NOT_EXISTS, null)
        }
        Log.d("Client-Review", "要查询的ReviewId = $reviewId")
//        val localReview = cacheRepository.getReview(reviewId)
//        if (localReview != null) {
//            Log.d("Client-Review", "从本地加载了Review${gson.toJson(localReview)}")
//            return ReviewResponse(true, null, localReview)
//        }
        val requestBody = "".toRequestBody(null)
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("id", reviewId)
            .addFormDataPart("targetPost", "")
            .addFormDataPart("username", "")
            .addFormDataPart("content", "")
            .addFormDataPart("images", "", requestBody)
            .build()
        val request = Request.Builder().url("$serverUrl/review/query").post(multipartBody).build()
        val reviewResponse = client.newCall(request).execute()
        val remoteReviewResponse =
            gson.fromJson(reviewResponse.body?.string(), ReviewResponse::class.java)
//        if (remoteReviewResponse.success!!) {
//            try {
//                cacheRepository.addReview(remoteReviewResponse.review!!)
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remoteReviewResponse
    }

    fun addUser(user: User): UserResponse {
        val requestBody = gson.toJson(user).toRequestBody(jsonMediaType.toMediaType())
        val request = Request.Builder().url("$serverUrl/user/add").post(requestBody).build()
        val userResponse = client.newCall(request).execute()
        val remoteUserResponse =
            gson.fromJson(userResponse.body?.string(), UserResponse::class.java)
//        if (remoteUserResponse.success!!) {
//            try {
//                cacheRepository.addUser(user)
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remoteUserResponse
    }

    fun updateUserInfo(
        username: String,
        nickname: String,
        personalSign: String,
        avatar: File
    ): UserInfoResponse {
        val avatarRequestBody = avatar.asRequestBody(MultipartBody.FORM)
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("nickname", nickname)
            .addFormDataPart("personalSign", personalSign)
            .addFormDataPart("avatar", avatar.name, avatarRequestBody)
            .build()
        val request =
            Request.Builder().url("$serverUrl/userInfo/update").post(multipartBody).build()
        val userInfoResponse = client.newCall(request).execute()
        val remoteUserInfoResponse =
            gson.fromJson(userInfoResponse.body?.string(), UserInfoResponse::class.java)
//        if (remoteUserInfoResponse.success!!) {
//            try {
//                cacheRepository.deleteUserInfo(
//                    UserInfo(
//                        username,
//                        nickname,
//                        personalSign,
//                        null,
//                        listOf(),
//                        listOf()
//                    )
//                )
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remoteUserInfoResponse
    }

//    fun deleteUserInfo(username: String) {
//        cacheRepository.deleteUserInfo(UserInfo(username, null, null, null, listOf(), listOf()))
//    }

    fun addReview(
        id: String,
        targetPost: String,
        username: String,
        content: String,
        images: List<File>
    ): ReviewResponse {
        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("id", id)
            .addFormDataPart("targetPost", targetPost)
            .addFormDataPart("username", username)
            .addFormDataPart("content", content)
        if (images.isEmpty()) {
            multipartBodyBuilder.addFormDataPart("images", "", "".toRequestBody(null))
        }
        for (image in images) {
            val currentImageRequestBody = image.asRequestBody(MultipartBody.FORM)
            multipartBodyBuilder.addFormDataPart("images", image.name, currentImageRequestBody)
        }
        val multipartBody = multipartBodyBuilder.build()
        val request = Request.Builder().url("$serverUrl/review/add").post(multipartBody).build()
        val reviewResponse = client.newCall(request).execute()
        val remoteReviewResponse =
            gson.fromJson(reviewResponse.body?.string(), ReviewResponse::class.java)
//        if (remoteReviewResponse.success!!) {
//            try {
//                cacheRepository.deletePost(Post(targetPost, "", "", "", "", listOf(), 0, listOf()))
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remoteReviewResponse
    }

    fun addPost(
        id: String,
        owner: String,
        title: String,
        content: String,
        images: List<File>
    ): PostResponse {
        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("id", id)
            .addFormDataPart("owner", owner)
            .addFormDataPart("title", title)
            .addFormDataPart("content", content)
        if (images.isEmpty()) {
            multipartBodyBuilder.addFormDataPart("images", "", "".toRequestBody(null))
        }
        for (image in images) {
            val currentImageRequestBody = image.asRequestBody(MultipartBody.FORM)
            multipartBodyBuilder.addFormDataPart("images", image.name, currentImageRequestBody)
        }
        val multipartBody = multipartBodyBuilder.build()
        val request = Request.Builder().url("$serverUrl/post/add").post(multipartBody).build()
        val postResponse = client.newCall(request).execute()
        val remotePostResponse =
            gson.fromJson(postResponse.body?.string(), PostResponse::class.java)
//        if (remotePostResponse.success!!) {
//            try {
//                cacheRepository.deleteUserInfo(UserInfo(owner, "", "", null, listOf(), listOf()))
//            } catch (e: Exception) {
//                Log.d("Client", "被捕获的异常: $e")
//            }
//        }
        return remotePostResponse
    }

    fun like(actionRequest: ActionRequest): ActionResponse {
        val requestBody = gson.toJson(actionRequest).toRequestBody(jsonMediaType.toMediaType())
        val request = Request.Builder().url("$serverUrl/action/like").post(requestBody).build()
        val actionResponse = client.newCall(request).execute()
        return gson.fromJson(actionResponse.body?.string(), ActionResponse::class.java)
    }

    private fun favor(username: String, targetPost: String, isCancelFavor: Boolean): ActionResponse {
        val requestBody =
            gson.toJson(
                ActionRequest(true, targetPost, null, username)
            ).toRequestBody(
                jsonMediaType.toMediaType()
            )
        Log.d("Client", "post: ${gson.toJson(
            ActionRequest(true, targetPost, "", username)
        )}")
        val request = Request.Builder()
            .url("$serverUrl/action/" + if (isCancelFavor) "cancelFavor" else "favor")
            .post(requestBody).build()
        val actionResponse = client.newCall(request).execute()
        return gson.fromJson(actionResponse.body?.string(), ActionResponse::class.java)
    }

    fun favor(username: String, targetPost: String): ActionResponse {
        return favor(username, targetPost, false)
    }

    fun cancelFavor(username: String, targetPost: String): ActionResponse {
        return favor(username, targetPost, true)
    }
}