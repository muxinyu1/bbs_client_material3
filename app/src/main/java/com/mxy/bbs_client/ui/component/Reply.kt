package com.mxy.bbs_client.ui.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.mxy.bbs_client.R
import com.mxy.bbs_client.entity.action.ActionRequest
import com.mxy.bbs_client.entity.review.Review
import com.mxy.bbs_client.program.state.ReviewState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import kotlinx.coroutines.launch

val replyCardBorderStroke = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.7f))

val DefaultReview = Review("", "", "", "加载中...", "", listOf(), 0);

private val onReviewLikeClick: (String?) -> Unit = {
    with(Utility.IOCoroutineScope) {
        launch {
            Log.d("mxy!!!", Gson().toJson(Client.like(ActionRequest(false, null, it))))
        }
    }
}

@Composable
fun Reply(
    avatarUrl: Any,
    nickname: String,
    isPostHost: Boolean,
    personalSign: String,
    content: String,
    modifier: Modifier,
    imgUrls: List<Any>,
    floor: Int,
    date: String,
    likeNum: Int,
    onLikeClick: (String) -> Unit
) {
    val likeNumState = remember {
        mutableStateOf(likeNum)
    }
    val iLikeState = remember {
        mutableStateOf(false)
    }
    likeNumState.value = likeNum
    OutlinedCard(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = modifier) {
            //用户头像和点赞数
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(alignment = CenterHorizontally)
                        .padding(10.dp)
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                )
                Column(modifier = Modifier.padding(10.dp)) {
                    IconButton(
                        onClick = {
                            if (!iLikeState.value) {
                                iLikeState.value = true
                                likeNumState.value++
                                /*TODO:喜欢该评论*/
                            }
                        },
                        modifier = Modifier.align(alignment = CenterHorizontally)
                    ) {
                        Icon(
                            painter = if (iLikeState.value)
                                painterResource(id = R.drawable.liked)
                            else painterResource(
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
            }
            Column(modifier = Modifier.padding(5.dp)) {
                //用户昵称和签名
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.align(alignment = CenterVertically)) {
                        Text(
                            text = nickname,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(alignment = CenterVertically)
                                .padding(5.dp)
                        )
                        //显楼主图标
                        if (isPostHost) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Post Host Tag",
                                tint = Color.Gray,
                                modifier = Modifier.align(alignment = CenterVertically)
                            )
                        }
                    }
                    Text(
                        text = personalSign,
                        color = Color.Gray.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .align(alignment = CenterVertically)
                            .padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                //回复帖子的内容
                Text(
                    text = content,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(5.dp)
                )
                //回复中的图片
                for (imgUrl in imgUrls) {
                    AsyncImage(
                        model = imgUrl, contentDescription = "reply img",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                //楼层数和回复时间
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "第${floor}楼",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun Reply(modifier: Modifier, floor: Int, isPostHost: Boolean, reviewState: ReviewState) {
    Reply(
        avatarUrl = reviewState.avatarUrl,
        nickname = reviewState.nickname,
        content = reviewState.content,
        modifier = modifier,
        imgUrls = reviewState.images,
        floor = floor,
        date = reviewState.date,
        personalSign = reviewState.personalSign,
        onLikeClick = onReviewLikeClick,
        likeNum = reviewState.likeNum,
        isPostHost = isPostHost
    )
}