package com.mxy.bbs_client.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mxy.bbs_client.program.state.PostState
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.MessageCircle
import compose.icons.feathericons.ThumbsUp

val cardCornerShape = RoundedCornerShape(3)
val avatarSize = 40.dp

@Composable
fun PostCard(
    postId: String,
    modifier: Modifier,
    avatarUrl: Any,
    nickname: String,
    date: String,
    reviewNum: Int,
    likeNum: Int,
    title: String,
    content: String,
    contentImgUrl: Any?,
    homeScreenViewModel: HomeScreenViewModel
) {
    OutlinedCard(
        shape = cardCornerShape,
        modifier = modifier.clickable { homeScreenViewModel.openPost(postId) },
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
fun PostCard(
    postState: PostState,
    modifier: Modifier,
    homeScreenViewModel: HomeScreenViewModel
) {
    PostCard(
        modifier = modifier,
        postId = postState.postId,
        avatarUrl = postState.avatarUrl,
        nickname = postState.nickname,
        date = postState.date,
        reviewNum = postState.reviewNum,
        likeNum = postState.likeNum,
        title = postState.title,
        content = postState.content,
        contentImgUrl = if (postState.images.isEmpty()) null else postState.images[0],
        homeScreenViewModel = homeScreenViewModel
    )
}

@Composable
fun UserAndDate(avatarUrl: Any, username: String, date: String) {
    Row(modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            SubcomposeAsyncImage(
                model = avatarUrl,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(5.dp)
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black.copy(alpha = 0.5f), CircleShape)
                    .align(alignment = CenterVertically)
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator()
                } else {
                    SubcomposeAsyncImageContent()
                }
            }
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(5.dp)
                    .align(alignment = CenterVertically)
            )
        }
        Text(
            text = date,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = CenterVertically)
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
private fun CardContent(title: String, content: String, contentImgUrl: Any?) {
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
            SubcomposeAsyncImage(
                model = contentImgUrl,
                contentScale = ContentScale.Crop,
                contentDescription = "content image",
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator()
                } else {
                    SubcomposeAsyncImageContent()
                }
            }
        }
    }

}