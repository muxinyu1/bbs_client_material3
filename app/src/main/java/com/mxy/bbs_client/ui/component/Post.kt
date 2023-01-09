@file:OptIn(ExperimentalMaterialApi::class)

package com.mxy.bbs_client.ui.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.mxy.bbs_client.R
import com.mxy.bbs_client.entity.action.ActionRequest
import com.mxy.bbs_client.entity.post.Post
import com.mxy.bbs_client.entity.userinfo.UserInfo
import com.mxy.bbs_client.serverinfo.DefaultAvatarUrl
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import de.charlex.compose.BottomDrawerScaffold
import de.charlex.compose.rememberBottomDrawerScaffoldState
import kotlinx.coroutines.launch

private val onPostLikeClick: (String?) -> Unit = {
    with(Utility.IOCoroutineScope) {
        launch {
            Log.d("mxy!!!", Gson().toJson(Client.like(ActionRequest(true, it, null))))
        }
    }
}

val DefaultPost = Post(
    "正在加载帖子",
    "",
    "加载中...",
    "加载中...",
    "加载中",
    listOf(),
    0,
    listOf()
)

val DefaultUserInfo = UserInfo("加载中...", "加载中...", "", DefaultAvatarUrl, listOf(), listOf())

const val AlreadyBottom = "到底了~~"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun Post(
    postId: String,
    postHost: String,
    avatarUrl: Any,
    nickname: String,
    date: String,
    modifier: Modifier,
    title: String,
    content: String,
    onLikeClick: (postId: String?) -> Unit,
    postImgUrls: List<Any>,
    likeNum: Int,
    iLike: Boolean,
    replyIds: List<String>
) {
    val iLikeState = remember { mutableStateOf(iLike) }
    val likeNumState = remember { mutableStateOf(likeNum) }
    likeNumState.value = likeNum
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        UserAndDate(avatarUrl = avatarUrl, username = nickname, date = date)
        Spacer(modifier = Modifier.height(5.dp))
        //帖子标题
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp)
        )
        //帖子内容
        Text(
            text = content, fontSize = 16.sp, modifier = Modifier.padding(5.dp)
        )
        //帖子图片
        for (imgUrl in postImgUrls) {
            AsyncImage(
                model = imgUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        //点赞图标
        Column(modifier = Modifier.padding(5.dp)) {
            IconButton(
                onClick = {
                    if (!iLikeState.value) {
                        iLikeState.value = true
                        likeNumState.value++
                        onLikeClick(postId)
                    }
                },
                modifier = Modifier.align(alignment = CenterHorizontally)
            ) {
                Icon(
                    painter = if (iLikeState.value) painterResource(id = R.drawable.liked) else painterResource(
                        id = R.drawable.like
                    ), contentDescription = "like"
                )
            }
            //点赞数
            Text(
                text = likeNumState.value.toString(),
                modifier = Modifier.align(alignment = CenterHorizontally)
            )
        }
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        var floor = 0
        for (replyId in replyIds) {
            Reply(replyId = replyId, Modifier.padding(5.dp), floor++, postHost = postHost)
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}

@Composable
fun Post(postId: String?, modifier: Modifier) {
    if (postId == null)
        return
    val postState = remember {
        mutableStateOf(DefaultPost)
    }
    val userInfoState = remember {
        mutableStateOf(DefaultUserInfo)
    }
    Post(
        postId = postId,
        avatarUrl = userInfoState.value.avatarUrl!!,
        nickname = userInfoState.value.nickname!!,
        date = postState.value.date!!,
        modifier = modifier,
        title = postState.value.title!!,
        content = postState.value.content!!,
        onLikeClick = onPostLikeClick,
        postImgUrls = postState.value.images,
        likeNum = postState.value.likeNum!!,
        iLike = false,
        replyIds = postState.value.reviews,
        postHost = userInfoState.value.username!!
    )
    with(Utility.IOCoroutineScope) {
        launch {
            postState.value = Client.getPost(postId).post!!
            userInfoState.value = Client.getUserInfo(postState.value.owner).userInfo!!
        }
    }
}