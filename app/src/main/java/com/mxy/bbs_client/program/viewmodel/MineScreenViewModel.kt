package com.mxy.bbs_client.program.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.program.state.MineScreenState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File

class MineScreenViewModel : ViewModel() {
    companion object {
        private const val NotLoginError = "请先登录"
        private const val PleaseTryAgain = "请再试一次"
        private const val PostSuccess = "发帖成功"
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

    private val _mineScreenState = MutableStateFlow(MineScreenState(false, null, 0))
    val mineScreenState = _mineScreenState.asStateFlow()
    fun loginSuccessfully(username: String) {
        _mineScreenState.value = MineScreenState(true, username, 0)
    }

    fun logout() {
        _mineScreenState.value = MineScreenState(false, null, 0)
    }

    fun refresh(username: String) {
        _mineScreenState.value =
            MineScreenState(true, username, 1 - _mineScreenState.value.placeholder)
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
}