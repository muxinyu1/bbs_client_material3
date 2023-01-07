package com.mxy.bbs_client.ui.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.mxy.bbs_client.R
import com.mxy.bbs_client.entity.action.ActionRequest
import com.mxy.bbs_client.entity.review.Review
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.launch

val replyCardBorderStroke = BorderStroke(1.dp, Color.Gray)

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
    replyId: String,
    avatarUrl: String,
    username: String,
    personalSign: String,
    content: String,
    modifier: Modifier,
    imgUrls: List<String>,
    floor: Int,
    date: String,
    likeNum: Int,
    onLikeClick: (String) -> Unit
) {
    Log.d("mxy!!!", "images = $imgUrls")
    Log.d("mxy!!!", "avatarUrl = $avatarUrl")
    val likeNumState = remember {
        mutableStateOf(likeNum)
    }
    val iLikeState = remember {
        mutableStateOf(false)
    }
    likeNumState.value = likeNum
    Card(
        shape = cardCornerShape,
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
        border = replyCardBorderStroke,
        elevation = cardShadowSz,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = modifier) {
            //用户头像和点赞数
            Column {
                Image(
                    painter = rememberAsyncImagePainter(model = avatarUrl),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black.copy(alpha = 0.5f), CircleShape)
                        .padding(5.dp)
                )
                Column(modifier = Modifier.padding(5.dp)) {
                    IconButton(onClick = {
                        if (!iLikeState.value) {
                            iLikeState.value = true
                            likeNumState.value++
                            onLikeClick(replyId)
                        }
                    }) {
                        Icon(
                            painter = if (iLikeState.value) painterResource(id = R.drawable.liked) else painterResource(
                                id = R.drawable.like
                            ), contentDescription = "like"
                        )
                    }
                    //点赞数
                    //Log.d("mxy!!!点赞", "点赞数 = ${likeNumState.value}")
                    Text(text = likeNumState.value.toString(), /*color = Color.Black.copy(alpha = 0.7f)*/)
                }
            }
            Column(modifier = Modifier.padding(5.dp)) {
                //用户昵称和签名
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = username,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = personalSign,
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(5.dp)
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
                    Log.d("mxy!!!", "画图: $imgUrl")
                    AsyncImage(model = imgUrl, contentDescription = "reply img",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                //楼层数和回复时间
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
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
fun Reply(replyId: String, modifier: Modifier, floor: Int) {
    val reviewState = remember {
        mutableStateOf(DefaultReview)
    }
    val userInfoState = remember {
        mutableStateOf(DefaultUserInfo)
    }
    Reply(
        avatarUrl = userInfoState.value.avatarUrl!!,
        username = userInfoState.value.nickname!!,
        content = reviewState.value.content!!,
        modifier = modifier,
        imgUrls = reviewState.value.images!!,
        floor = floor,
        date = reviewState.value.date!!,
        personalSign = userInfoState.value.personalSign!!,
        replyId = replyId,
        onLikeClick = onReviewLikeClick,
        likeNum = reviewState.value.likeNum!!
    )
    with(Utility.IOCoroutineScope) {
        launch {
            reviewState.value = Client.getReview(replyId).review!!
            userInfoState.value = Client.getUserInfo(reviewState.value.username).userInfo!!
        }
    }
}

@Composable
@Preview
fun ReplyPreview() {
    Reply(
        avatarUrl = "https://i0.hdslb.com/bfs/face/af43df9322362f199ef6e752192786705acfe704.jpg@240w_240h_1c_1s.webp",
        username = "昊京",
        content = "和蔼！任何邪恶，终将绳之以法！！！大哥新到手的收集，便宜；你这是违法行为，走跟我去自首",
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        imgUrls = listOf("https://img.liaobagua.com/uploads/webp/article/20220826/1661523776478643.webp"),
        floor = 114514,
        date = "10月1212日",
        personalSign = "",
        replyId = "dsd",
        onLikeClick = {},
        likeNum = 0
    )
}