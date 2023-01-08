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
    postId: String?,
    avatarUrl: String,
    nickname: String,
    date: String,
    modifier: Modifier,
    title: String,
    content: String,
    onLikeClick: (postId: String?) -> Unit,
    postImgUrls: List<String>,
    likeNum: Int,
    iLike: Boolean,
    replyIds: List<String>
) {
    val coroutineScope = rememberCoroutineScope()
    val iLikeState = remember { mutableStateOf(iLike) }
    val likeNumState = remember { mutableStateOf(likeNum) }
    likeNumState.value = likeNum
    val scaffoldState = rememberBottomDrawerScaffoldState()
    BottomDrawerScaffold(
        drawerPeekHeight = 20.dp,
        scaffoldState = scaffoldState,
        drawerContent = { },
        drawerShape = RoundedCornerShape(20.dp),
    ) {
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
                Log.d("mxy!!!", "准备加载$imgUrl")
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
                IconButton(onClick = {
                    if (!iLikeState.value) {
                        iLikeState.value = true
                        likeNumState.value++
                        onLikeClick(postId)
                    }
                }) {
                    Icon(
                        painter = if (iLikeState.value) painterResource(id = R.drawable.like) else painterResource(
                            id = R.drawable.liked
                        ), contentDescription = "like"
                    )
                }
                //点赞数
                Text(text = likeNumState.value.toString(), color = Color.Black.copy(alpha = 0.7f))
            }
            Divider()
            Spacer(modifier = Modifier.height(20.dp))
            var floor = 0
            for (replyId in replyIds) {
                Reply(replyId = replyId, Modifier.padding(5.dp), floor++)
                Spacer(modifier = Modifier.height(20.dp))
            }

        }
    }

//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    coroutineScope.launch {
//                        if (drawerState.isClosed)
//                            drawerState.open()
//                        else
//                            drawerState.close();
//                        Log.d("bottom drawer", "open")
//                    }
//                },
//                containerColor = Color.White,
//                modifier = Modifier.size(80.dp)
//            ) {
//                Icon(Icons.Filled.Edit, contentDescription = "Reply")
//            }
//        },
//    ) {
//        Column {
//            BottomDrawer(
//                drawerContent = { AddContentPreview() },
//                drawerState = drawerState,
//                drawerShape = RoundedCornerShape(20.dp),
//                gesturesEnabled = true,
//            ) {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    content = {}
//                )
//            }
//            Column(modifier = modifier.verticalScroll(scrollState)) {
//                UserAndDate(avatarUrl = avatarUrl, username = nickname, date = date)
//                Spacer(modifier = Modifier.height(5.dp))
//                //帖子标题
//                Text(
//                    text = title,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(5.dp)
//                )
//                //帖子内容
//                Text(
//                    text = content, fontSize = 16.sp, modifier = Modifier.padding(5.dp)
//                )
//                //帖子图片
//                for (imgUrl in postImgUrls) {
//                    Log.d("mxy!!!", "准备加载$imgUrl")
//                    AsyncImage(
//                        model = imgUrl,
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(10.dp))
//                            .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
//                            .fillMaxWidth(),
//                    )
//                    Spacer(modifier = Modifier.height(20.dp))
//                }
//                //点赞图标
//                Column(modifier = Modifier.padding(5.dp)) {
//                    IconButton(onClick = {
//                        if (!iLikeState.value) {
//                            iLikeState.value = true
//                            likeNumState.value++
//                            onLikeClick(postId)
//                        }
//                    }) {
//                        Icon(
//                            painter = if (iLikeState.value) painterResource(id = R.drawable.ic_i_like) else painterResource(
//                                id = R.drawable.iconmonstr_thumb_10
//                            ), contentDescription = "like"
//                        )
//                    }
//                    //点赞数
//                    Text(text = likeNumState.value.toString(), color = Color.Black.copy(alpha = 0.7f))
//                }
//                Divider()
//                Spacer(modifier = Modifier.height(20.dp))
//                var floor = 0
//                for (replyId in replyIds) {
//                    Reply(replyId = replyId, Modifier.padding(5.dp), floor++)
//                    Spacer(modifier = Modifier.height(20.dp))
//                }
//
//            }
//        }
//
//    }
}

/**
 * 还在加载中的Post(默认Post)
 */
@Composable
fun Post() {
    //TODO:修改默认Post样式
    Text(text = "默认Post")
}

@Composable
fun Post(postId: String, modifier: Modifier) {
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
        replyIds = postState.value.reviews
    )
    with(Utility.IOCoroutineScope) {
        launch {
            postState.value = Client.getPost(postId).post!!
            userInfoState.value = Client.getUserInfo(postState.value.owner).userInfo!!
        }
    }
}