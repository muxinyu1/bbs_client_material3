package com.mxy.bbs_client.program.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.gson.Gson
import com.mxy.bbs_client.program.db.MineScreenStateDataBase
import com.mxy.bbs_client.program.repository.MineScreenStateRepository
import com.mxy.bbs_client.program.state.MineScreenState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File

class MineScreenViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val NotLoginError = "请先登录"
        private const val PleaseTryAgain = "请再试一次"
        private const val PostSuccess = "发帖成功"
        private const val ReviewSuccess = "评论成功"
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
            return res.toList()
        }
    }

    private val mineScreenStateDataBase by lazy {
        Room.databaseBuilder(
            context = app,
            klass = MineScreenStateDataBase::class.java,
            name = "data.db"
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
                    username = null,
                    placeholder = 0
                )
                _mineScreenState = MutableStateFlow(notLogin)
                mineScreenStateRepository.add(notLogin)
            }
            mineScreenState = _mineScreenState.asStateFlow()
        }
    }


    private lateinit var _mineScreenState: MutableStateFlow<MineScreenState>

    lateinit var  mineScreenState: StateFlow<MineScreenState>

    fun loginSuccessfully(username: String) {
        val login = MineScreenState(login = true, username = username, placeholder = 0)
        _mineScreenState.value = login
        viewModelScope.launch {
            mineScreenStateRepository.update(login)
        }
    }

    fun logout() {
        val logout = MineScreenState(login = false, username = null, placeholder = 0)
        _mineScreenState.value = logout
        viewModelScope.launch {
            mineScreenStateRepository.update(logout)
        }
    }

    fun refresh(username: String) {
        _mineScreenState.value =
            MineScreenState(
                login = true,
                username = username,
                placeholder = 1 - _mineScreenState.value.placeholder
            )
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
                    _mineScreenState.value.username!!,
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
                    refresh(_mineScreenState.value.username!!)
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
                    //已经登录后username不可能是null
                    _mineScreenState.value.username!!,
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
                    }
                }
            }
        }
    }

    fun closeDatabase() {
        mineScreenStateRepository.closeDataBase()
    }
}