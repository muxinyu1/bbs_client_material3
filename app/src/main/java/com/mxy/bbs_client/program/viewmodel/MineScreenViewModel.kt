@file:OptIn(ExperimentalMaterialApi::class)

package com.mxy.bbs_client.program.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.gson.Gson
import com.mxy.bbs_client.entity.user.User
import com.mxy.bbs_client.program.db.MineScreenStateDataBase
import com.mxy.bbs_client.program.repository.MineScreenStateRepository
import com.mxy.bbs_client.program.state.DefaultUserInfoState
import com.mxy.bbs_client.program.state.MineScreenState
import com.mxy.bbs_client.program.state.UserInfoState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

class MineScreenViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val NotLoginError = "请先登录"
        private const val PleaseTryAgain = "请再试一次"
        private const val PostSuccess = "发帖成功"
        private const val ReviewSuccess = "评论成功"
        private const val UsernameDoesNotExistError = "用户名不存在"
        private const val WrongPasswordError = "用户名或密码错误"
        private const val UsernameAlreadyExistError = "用户名已存在"
        private const val LoginSuccess = "登录成功"
        private const val SignUpSuccess = "注册成功"
        private const val ModifySuccess = "更改成功"
        private fun toFile(uri: Uri, context: Context): File {
            val file = File.createTempFile(Utility.getRandomString(), null)
            val inputStream = context.contentResolver.openInputStream(uri)
            FileUtils.copyInputStreamToFile(inputStream, file)
            return file
        }

        private fun toFiles(uriList: List<Uri>, context: Context): List<File> {
            val res = mutableListOf<File>()
            for (uri in uriList) {
                res.add(toFile(uri, context))
            }
            if (uriList.isEmpty()) {
                Log.d("toFiles!!?", "uriList isEmpty")
            } else {
                Log.d("toFiles!!?", "麻麻的为什么会有图片")
            }
            return res.toList()
        }
    }

    private val mineScreenStateDataBase by lazy {
        Room.databaseBuilder(
            context = app,
            klass = MineScreenStateDataBase::class.java,
            name = "login_data.db"
        ).fallbackToDestructiveMigration().build()
    }

    private val mineScreenStateRepository = MineScreenStateRepository(mineScreenStateDataBase)

    init {
        viewModelScope.launch {
            val stateInDateBase = mineScreenStateRepository.getMineScreenState()
            if (stateInDateBase != null) {
                _mineScreenState = MutableStateFlow(stateInDateBase)
            } else {
                //第一次启动App
                val notLogin = MineScreenState(
                    login = false,
                    placeholder = 0,
                    userInfoState = DefaultUserInfoState
                )
                _mineScreenState = MutableStateFlow(notLogin)
                mineScreenStateRepository.add(notLogin)
            }
            mineScreenState = _mineScreenState.asStateFlow()
        }
    }

    private lateinit var _mineScreenState: MutableStateFlow<MineScreenState>

    private val _bottomSheetState = MutableStateFlow(false)

    val bottomSheetState = _bottomSheetState.asStateFlow()

    fun openBottomSheet() {
        _bottomSheetState.value = true
    }

    private fun closeBottomSheet()  {
        _bottomSheetState.value = false
    }

    lateinit var mineScreenState: StateFlow<MineScreenState>

    private fun loginSuccessfully(username: String) = with(Utility.IOCoroutineScope) {
        launch {
            //登录成功说明userinfo一定存在
            val userInfo = Client.getUserInfo(username).userInfo!!
            val postStateListSent = HomeScreenViewModel.toPostStateList(userInfo.myPosts)
            val postStateCollected = HomeScreenViewModel.toPostStateList(userInfo.myCollections)
            val userInfoState = UserInfoState(
                username = username,
                nickname = userInfo.nickname!!,
                avatarUrl = userInfo.avatarUrl!!,
                personalSign = userInfo.personalSign!!,
                myPosts = postStateListSent,
                myCollections = postStateCollected
            )
            val mineScreenStateLogin = MineScreenState(
                login = true,
                placeholder = 0,
                userInfoState = userInfoState
            )
            _mineScreenState.value = mineScreenStateLogin
            mineScreenStateRepository.update(mineScreenStateLogin)
        }
    }


    fun login(username: String, password: String, context: Context) =
        with(Utility.IOCoroutineScope) {
            launch {
                val userResponse = Client.getUser(username)
                if (!userResponse.success!!) {
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, UsernameDoesNotExistError, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    val user = userResponse.user!!
                    if (password != user.password) {
                        with(Utility.UICoroutineScope) {
                            launch {
                                Toast.makeText(context, WrongPasswordError, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        with(Utility.UICoroutineScope) {
                            launch {
                                Toast.makeText(context, LoginSuccess, Toast.LENGTH_SHORT).show()
                            }
                        }
                        loginSuccessfully(username)
                    }
                }
            }
        }


    fun signUp(
        username: String,
        password: String,
        context: Context
    ) =
        with(Utility.IOCoroutineScope) {
            launch {
                val userResponse = Client.addUser(User(username, password))
                if (!userResponse.success!!) {
                    //用户名已存在
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, UsernameAlreadyExistError, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    //注册成功
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, SignUpSuccess, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //自动登录
                    loginSuccessfully(username)
                }
            }
        }


    fun logout() {
        val logout =
            MineScreenState(login = false, placeholder = 0, userInfoState = DefaultUserInfoState)
        _mineScreenState.value = logout
        with(Utility.IOCoroutineScope) {
            launch {
                mineScreenStateRepository.update(logout)
                // Client.deleteUserInfo(_mineScreenState.value.userInfoState.username)
            }
        }
    }

    fun updateUserInfo(
        context: Context,
        newNickname: String,
        newPersonalSign: String,
        newAvtarUri: Uri?,
    ) {
        val username = _mineScreenState.value.userInfoState.username
        with(Utility.IOCoroutineScope) {
            launch {
                val avatarFile = if (newAvtarUri != null) {
                    val inputStream = context.contentResolver.openInputStream(newAvtarUri)
                    val avatarTmpFile = withContext(Dispatchers.IO) {
                        File.createTempFile("tmpAvatar", null)
                    }
                    FileUtils.copyInputStreamToFile(inputStream, avatarTmpFile)
                    avatarTmpFile
                } else {
                    val avatarTmpFile =
                        withContext(Dispatchers.IO) {
                            File.createTempFile("tmpAvatar", null)
                        }
                    val avatarUrl = _mineScreenState.value.userInfoState.avatarUrl
                    FileUtils.copyURLToFile(URL(avatarUrl as String?), avatarTmpFile)
                    avatarTmpFile
                }
                val userInfoResponse = Client.updateUserInfo(
                    username,
                    newNickname,
                    newPersonalSign,
                    avatarFile
                )
                with(Utility.UICoroutineScope) {
                    launch {
                        if (userInfoResponse.success != null && userInfoResponse.success) {
                            Toast.makeText(context, ModifySuccess, Toast.LENGTH_SHORT).show()
                            refresh(username)
                        }
                    }
                }
            }
        }
    }

    private fun refresh(username: String) {
        if (!_mineScreenState.value.login) {
            return
        }
        loginSuccessfully(username)
    }

    fun sendPost(title: String, content: String, images: List<Uri>, context: Context) {
        if (!_mineScreenState.value.login) {
            with(Utility.UICoroutineScope) {
                launch {
                    Toast.makeText(context, NotLoginError, Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        with(Utility.IOCoroutineScope) {
            launch {
                val imageList = toFiles(images, context)
                val postResponse = Client.addPost(
                    Utility.getRandomString(),
                    //已经登录后username不可能是null
                    _mineScreenState.value.userInfoState!!.username,
                    title,
                    content,
                    imageList
                )
                if (!postResponse.success!!) {
                    //postId有极小的概率会重复
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, PleaseTryAgain, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, PostSuccess, Toast.LENGTH_SHORT).show()
                        }
                    }
                    refresh(_mineScreenState.value.userInfoState.username)
                    closeBottomSheet()
                }
            }
        }
    }

    fun sendReview(
        content: String,
        images: List<Uri>,
        context: Context,
        targetPost: String,
        homeScreenViewModel: HomeScreenViewModel
    ) {
        if (!_mineScreenState.value.login) {
            with(Utility.UICoroutineScope) {
                launch {
                    Toast.makeText(context, NotLoginError, Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        with(Utility.IOCoroutineScope) {
            launch {
                val imageList = toFiles(images, context)
                val reviewResponse = Client.addReview(
                    Utility.getRandomString(),
                    targetPost,
                    _mineScreenState.value.userInfoState.username,
                    content,
                    imageList
                )
                Log.d("sendReview", Gson().toJson(reviewResponse))
                if (!reviewResponse.success!!) {
                    //postId有极小的概率会重复
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, PleaseTryAgain, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, ReviewSuccess, Toast.LENGTH_SHORT).show()
                        }
                        homeScreenViewModel.refreshPost()
                        closeBottomSheet()
                    }
                }
            }
        }
    }

    fun closeDatabase() {
        mineScreenStateRepository.closeDataBase()
    }
}