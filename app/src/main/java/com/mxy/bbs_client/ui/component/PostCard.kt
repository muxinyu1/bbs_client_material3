package com.mxy.bbs_client.ui.component

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mxy.bbs_client.R
import com.mxy.bbs_client.program.ProgramState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.MessageCircle
import compose.icons.feathericons.ThumbsUp
import kotlinx.coroutines.launch

val cardShadowSz = 5.dp;
val cardBackgroundColor = Color.White
val cardCornerShape = RoundedCornerShape(3)
val avatarSize = 40.dp

private val onPostCardClick: (String) -> Unit = {
    ProgramState.PostState.openedPost = it
    Log.d("PostCard", "openedPost = $it")
}

@Composable
fun PostCard(
    postId: String,
    modifier: Modifier,
    avatarUrl: String,
    nickname: String,
    date: String,
    reviewNum: Int,
    likeNum: Int,
    title: String,
    content: String,
    contentImgUrl: String?,
    onPostCardClick: (String) -> Unit
) {
    OutlinedCard(
        shape = cardCornerShape,
        modifier = modifier.clickable { onPostCardClick(postId) },
        //border = cardBorderStroke,
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Spacer(modifier = Modifier.height(5.dp))
            UserAndDate(avatarUrl = avatarUrl, username = nickname, date = date)
            Spacer(modifier = Modifier.height(5.dp))
            CardContent(title = title, content = content, contentImgUrl = contentImgUrl)
            Spacer(modifier = Modifier.height(5.dp))
            ReviewAndLike(reviewNum = reviewNum, likeNum = likeNum)
        }
    }
}

@Composable
fun PostCard(postId: String, modifier: Modifier) {
    val defaultUserInfoState = remember {
        mutableStateOf(DefaultUserInfo)
    }
    val defaultPostState = remember {
        mutableStateOf(DefaultPost)
    }
    PostCard(
        postId = postId,
        modifier = modifier,
        avatarUrl = defaultUserInfoState.value.avatarUrl!!,
        nickname = defaultUserInfoState.value.nickname!!,
        date = defaultPostState.value.date!!,
        reviewNum = defaultPostState.value.reviews.size,
        likeNum = defaultPostState.value.likeNum!!,
        title = defaultPostState.value.title!!,
        content = defaultPostState.value.content!!,
        contentImgUrl = if (defaultPostState.value.images.isEmpty()) null else defaultPostState.value.images[0],
        onPostCardClick = onPostCardClick
    )
    with(Utility.IOCoroutineScope) {
        launch {
            defaultPostState.value = Client.getPost(postId).post!!
            defaultUserInfoState.value = Client.getUserInfo(defaultPostState.value.owner).userInfo!!
        }
    }
}

@Composable
fun UserAndDate(avatarUrl: String, username: String, date: String) {
    Row(modifier = Modifier.padding(5.dp)) {
        AsyncImage(
            model = avatarUrl, contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(1.dp, Color.Black.copy(alpha = 0.5f), CircleShape)
                .padding(5.dp)
        )
        Text(
            text = username,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            text = date,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
private fun ReviewAndLike(reviewNum: Int, likeNum: Int) {
    Row(modifier = Modifier.padding(5.dp)) {
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.weight(1f)) {
            Icon(
                FeatherIcons.MessageCircle,
                contentDescription = "review"
            )
            Text(
                text = "$reviewNum",
                modifier = Modifier.padding(5.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.weight(1f)) {
            Icon(
                FeatherIcons.ThumbsUp,
                contentDescription = "like"
            )
            Text(
                text = "$likeNum",
                modifier = Modifier.padding(5.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CardContent(title: String, content: String, contentImgUrl: String?) {
    Column(modifier = Modifier.padding(5.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = content,
            fontSize = 16.sp,
            modifier = Modifier.padding(5.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        if (contentImgUrl != null) {
            AsyncImage(
                model = contentImgUrl,
                contentScale = ContentScale.Crop,
                contentDescription = "content image",
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
            )
        }
    }

}

@Composable
@Preview
fun PostCardPreview() {
    PostCard(postId = "尴尬的铁根er的帖子给对方", modifier = Modifier.padding(5.dp))
}