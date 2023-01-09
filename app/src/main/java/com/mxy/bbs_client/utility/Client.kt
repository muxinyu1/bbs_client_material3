package com.mxy.bbs_client.utility

import android.app.Application
import android.util.Log
import androidx.room.Room
import coil.ImageLoader
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
import com.mxy.bbs_client.program.db.CacheDatabase
import com.mxy.bbs_client.program.repository.CacheRepository
import com.mxy.bbs_client.serverinfo.jsonMediaType
import com.mxy.bbs_client.serverinfo.serverUrl
import kotlinx.coroutines.launch
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

    lateinit var imageLoader: ImageLoader

    fun injectImageLoader(loader: ImageLoader) {
        imageLoader = loader
    }

    private lateinit var cacheDatabase: CacheDatabase

    private lateinit var cacheRepository: CacheRepository

    fun createCacheDatabase(app: Application) {
        cacheDatabase = Room.databaseBuilder(app, CacheDatabase::class.java, "app_data.db")
            .fallbackToDestructiveMigration().build()
        cacheRepository = CacheRepository(cacheDatabase)
    }

    fun closeDatabase() {
        cacheRepository.closeDatabase()
    }

    private val gson by lazy {
        Gson()
    }

    fun getUser(username: String?): UserResponse {
        if (username == null) {
            return UserResponse(false, UserResponseFailedReason.USERNAME_DOES_NOT_EXIST, null)
        }
        val localUser = cacheRepository.getUser(username)
        if (localUser != null) {
            Log.d("缓存", "本地存在${gson.toJson(localUser)}")
            return UserResponse(true, null, localUser)
        }
        val requestBody =
            gson.toJson(User(username, null)).toRequestBody(jsonMediaType.toMediaType())
        val request = Request.Builder().url("$serverUrl/user/query").post(requestBody).build()
        val userResponse = client.newCall(request).execute()
        val remoteUserResponse =
            gson.fromJson(userResponse.body?.string(), UserResponse::class.java)
        if (remoteUserResponse.success!!) {
            cacheRepository.addUser(remoteUserResponse.user!!)
        }
        return remoteUserResponse
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
        val localPost = cacheRepository.getPost(postId)
        if (localPost != null) {
            Log.d("缓存", "本地存在${gson.toJson(localPost)}")
            return PostResponse(true, null, localPost)
        }
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
        if (remotePostResponse.success!!) {
            cacheRepository.addPost(remotePostResponse.post!!)
        }
        return remotePostResponse
    }

    fun getUserInfo(username: String?): UserInfoResponse {
        if (username == null) {
            return UserInfoResponse(false, UserResponseFailedReason.USERNAME_DOES_NOT_EXIST, null)
        }
        val requestBody = "".toRequestBody(null)
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("nickname", "")
            .addFormDataPart("personalSign", "")
            .addFormDataPart("avatar", "", requestBody)
            .build()
        val request = Request.Builder().url("$serverUrl/userInfo/query").post(multipartBody).build()
        val userInfoResponse = client.newCall(request).execute()
        return gson.fromJson(userInfoResponse.body?.string(), UserInfoResponse::class.java)
    }

    fun getReview(reviewId: String?): ReviewResponse {
        if (reviewId == null) {
            return ReviewResponse(false, ReviewResponseFailedReason.REVIEW_DOES_NOT_EXISTS, null)
        }
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
        return gson.fromJson(reviewResponse.body?.string(), ReviewResponse::class.java)
    }

    fun addUser(user: User): UserResponse {
        val requestBody = gson.toJson(user).toRequestBody(jsonMediaType.toMediaType())
        val request = Request.Builder().url("$serverUrl/user/add").post(requestBody).build()
        val userResponse = client.newCall(request).execute()
        return gson.fromJson(userResponse.body?.string(), UserResponse::class.java)
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
        return gson.fromJson(userInfoResponse.body?.string(), UserInfoResponse::class.java)
    }

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
        return gson.fromJson(reviewResponse.body?.string(), ReviewResponse::class.java)
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
        return gson.fromJson(postResponse.body?.string(), PostResponse::class.java)
    }

    fun like(actionRequest: ActionRequest): ActionResponse {
        val requestBody = gson.toJson(actionRequest).toRequestBody(jsonMediaType.toMediaType())
        val request = Request.Builder().url("$serverUrl/action/like").post(requestBody).build()
        val actionResponse = client.newCall(request).execute()
        return gson.fromJson(actionResponse.body?.string(), ActionResponse::class.java)
    }
}