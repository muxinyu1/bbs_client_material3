@file:OptIn(ExperimentalMaterialApi::class)

package com.mxy.bbs_client.ui.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.gson.Gson
import com.mxy.bbs_client.R
import com.mxy.bbs_client.entity.action.ActionRequest
import com.mxy.bbs_client.entity.post.Post
import com.mxy.bbs_client.entity.userinfo.UserInfo
import com.mxy.bbs_client.program.state.PostState
import com.mxy.bbs_client.program.state.ReviewState
import com.mxy.bbs_client.serverinfo.DefaultAvatarUrl
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
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
    avatarUrl: Any,
    postOwner: String,
    nickname: String,
    date: String,
    modifier: Modifier,
    title: String,
    content: String,
    postImgUrls: List<Any>,
    likeNum: Int,
    iLike: Boolean,
    reviewStates: List<ReviewState>
) {
    val iLikeState = remember { mutableStateOf(iLike) }
    val likeNumState = remember { mutableStateOf(likeNum) }
    likeNumState.value = likeNum
    LazyColumn(modifier = modifier) {
        item {
            Column {
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
                    SubcomposeAsyncImage(
                        model = imgUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .fillMaxWidth(),
                    ) {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                            CircularProgressIndicator()
                        } else {
                            SubcomposeAsyncImageContent()
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                //点赞图标
                Column(modifier = Modifier.padding(5.dp)) {
                    IconButton(
                        onClick = {
                            if (!iLikeState.value) {
                                iLikeState.value = true
                                likeNumState.value++
                                //点赞
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
            }
        }
        items(reviewStates.size) {
            Reply(
                modifier = Modifier.padding(5.dp),
                floor = it,
                isPostHost = reviewStates[it].reviewOwner == postOwner,
                reviewState = reviewStates[it]
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun Post(modifier: Modifier, postState: PostState) {
    Post(
        avatarUrl = postState.avatarUrl,
        postOwner = postState.owner,
        nickname = postState.nickname,
        date = postState.date,
        modifier = modifier,
        title = postState.title,
        content = postState.content,
        postImgUrls = postState.images,
        likeNum = postState.likeNum,
        iLike = false,
        reviewStates = postState.reviews
    )
}