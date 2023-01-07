@file:OptIn(ExperimentalMaterial3Api::class)

package com.mxy.bbs_client.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mxy.bbs_client.R
import com.mxy.bbs_client.program.ProgramState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.Image
import compose.icons.feathericons.Smile
import kotlinx.coroutines.launch
import java.io.File

private const val EnterTitle = "帖子标题"
private const val EnterPostContent = "帖子内容"
private const val EnterReviewContent = "评论内容"
private const val ReleasePostText = "发布帖子"
private const val ReleaseReviewText = "发表回复"

private val onSendClick: (Boolean,  String, String, List<File>) -> Unit =
    { isPost, titleValue, contentValue, images ->
        if (titleValue == "" || contentValue == "") {
            //TODO:内容和标题不能是空

        } else {
            with(Utility.IOCoroutineScope) {
                launch {
                    if (isPost) {
                        val postResponse = Client.addPost(Utility.getRandomString(), ProgramState.UserInfoState.username, titleValue, contentValue, images)
                        if (postResponse.success == null || !postResponse.success) {
                            //TODO:发帖失败
                        } else {

                        }
                    } else {
                        val reviewResponse = Client.addReview(Utility.getRandomString(), ProgramState.PostState.openedPost, ProgramState.UserInfoState.username, contentValue, images)
                        if (reviewResponse.success == null || !reviewResponse.success) {
                            //TODO:回复帖子失败
                        } else {

                        }
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddContent(isPost: Boolean, modifier: Modifier) {
    val titleContentState = remember {
        mutableStateOf("")
    }
    val contentState = remember {
        mutableStateOf("")
    }
    val imagesState = remember {
        mutableStateOf(listOf<File>())
    }
    val onTitleContentChange: (String) -> Unit = {
        titleContentState.value = it
    }
    val onContentChange: (String) -> Unit = {
        contentState.value = it
    }
    Scaffold(
        bottomBar = {
            PictureEmojiAndSend(
                isPost,
                titleContentState.value,
                contentState.value,
                imagesState.value,
                onSendClick
            )
        },
        content = {
            Column(modifier = modifier) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isPost) ReleasePostText else ReleaseReviewText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))
                //如果是发新帖的话, 有个添加标题选项
                if (isPost) {
                    EnterTitle(
                        modifier = Modifier.padding(5.dp),
                        titleValue = titleContentState.value,
                        onValueChange = onTitleContentChange
                    )
                    Divider()
                }
                //输入帖子或回复内容
                EnterText(
                    placeholder = if (isPost) EnterPostContent else EnterReviewContent,
                    modifier = Modifier.padding(5.dp),
                    value = contentState.value,
                    onValueChange = onContentChange
                )
            }
        }
    )
}

@Composable
private fun EnterTitle(modifier: Modifier, titleValue: String, onValueChange: (String) -> Unit) =
    EnterText(
        placeholder = EnterTitle,
        modifier = modifier,
        value = titleValue,
        onValueChange = onValueChange
    )


@Composable
private fun PictureEmojiAndSend(
    isPost: Boolean,
    titleValue: String,
    contentValue: String,
    images: List<File>,
    onSendClick: (Boolean, String, String, List<File>) -> Unit
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    FeatherIcons.Smile,
                    contentDescription = "Picture Action"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    FeatherIcons.Image,
                    contentDescription = "Emoji Action",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.border(1.dp, Color.Black, FloatingActionButtonDefaults.shape),
                containerColor = Color.Transparent,
                onClick = { onSendClick(isPost, titleValue, contentValue, images) },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Send, "Send Content")
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
    )
}

@Preview(showBackground = true)
@Composable
fun AddContentPreview() {
    AddContent(isPost = true, modifier = Modifier.padding(5.dp))
}