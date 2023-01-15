@file:OptIn(ExperimentalMaterial3Api::class)

package com.mxy.bbs_client.ui.component

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.gson.Gson
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Image
import compose.icons.feathericons.Minus
import compose.icons.feathericons.Plus
import compose.icons.feathericons.Smile

private const val EnterTitle = "帖子标题"
private const val EnterPostContent = "帖子内容"
private const val EnterReviewContent = "评论内容"
private const val ReleasePostText = "发布帖子"
private const val ReleaseReviewText = "发表回复"
private const val TitleEmptyError = "标题不能为空"
private const val ContentEmptyError = "内容不能为空"
private const val SelectImages = "image/*"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddContent(
    isPost: Boolean,
    modifier: Modifier,
    mineScreenViewModel: MineScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    targetPost: String? = null
) {
    var title by remember {
        mutableStateOf("")
    }
    var content by remember {
        mutableStateOf("")
    }
    var titleIsEmpty by remember {
        mutableStateOf(true)
    }
    var contentIsEmpty by remember {
        mutableStateOf(true)
    }
    var imageList by remember {
        mutableStateOf(listOf<Uri>())
    }
    var showError by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val imagesPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) {
            imageList = imageList + it
        }
    Scaffold(
        bottomBar = {
            PictureEmojiAndSend(
                onSelectImgClick = {
                    imagesPickerLauncher.launch(SelectImages)
                },
                onSendClick = {
                    val hasError = if (isPost) (titleIsEmpty || contentIsEmpty) else contentIsEmpty
                    if (hasError) {
                        showError = true
                        return@PictureEmojiAndSend
                    }
                    if (isPost) {
                        mineScreenViewModel.sendPost(
                            title = title,
                            content = content,
                            images = imageList,
                            context = context
                        )
                    } else {
                        mineScreenViewModel.sendReview(
                            content = content,
                            images = imageList,
                            context = context,
                            //如果打开的是发表评论,那么targetPost不可能是null
                            targetPost = targetPost!!,
                            homeScreenViewModel = homeScreenViewModel
                        )
                    }
                }
            )
        },
        content = {
            Column(modifier = modifier) {
                Divider(
                    modifier = Modifier
                        .height(4.dp)
                        .width(60.dp)
                        .align(alignment = CenterHorizontally)
                        .clip(
                            RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isPost) ReleasePostText else ReleaseReviewText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(alignment = CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))
                //如果是发新帖的话, 有个添加标题选项
                if (isPost) {
                    EnterTitle(
                        modifier = Modifier.padding(5.dp),
                        titleValue = title,
                        onValueChange = {
                            titleIsEmpty = it.isBlank()
                            title = it
                        },
                        titleIsEmpty = if (showError) titleIsEmpty else false,
                        showError = showError
                    )
                    Divider()
                }
                //输入帖子或回复内容
                EnterText(
                    placeholder = if (isPost) EnterPostContent else EnterReviewContent,
                    modifier = Modifier.padding(5.dp),
                    value = content,
                    onValueChange = {
                        contentIsEmpty = it.isBlank()
                        content = it
                    },
                    isError = if (showError) contentIsEmpty else false,
                    supportingText = {
                        if (showError) {
                            AnimatedText(visible = contentIsEmpty, text = ContentEmptyError)
                        }
                    },
                    singleLine = false
                )
                //帖子或回复中包含的图片
                ImagesSet(
                    modifier = Modifier.padding(5.dp),
                    imageList = imageList,
                    onAddImgClick = {
                        imagesPickerLauncher.launch(SelectImages)
                    },
                    onRemoveImgClick = {
                        if (imageList.isNotEmpty()) {
                            imageList = if (imageList.size == 1) {
                                listOf()
                            } else {
                                imageList.subList(1, imageList.size)
                            }
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun ImagesSet(
    modifier: Modifier,
    imageList: List<Uri>,
    onAddImgClick: () -> Unit,
    onRemoveImgClick: () -> Unit
) {
    LazyRow(modifier = modifier, contentPadding = PaddingValues(5.dp)) {
        item {
            OutlinedIconButton(
                shape = RoundedCornerShape(15.dp),
                onClick = { onRemoveImgClick() },
                modifier = Modifier
                    .size(95.dp)
                    .padding(10.dp)
            ) {
                Icon(FeatherIcons.Minus, contentDescription = "Remove image")
            }
        }
        items(imageList) {
            //TODO:选择的图片应该改成圆角的
            SubcomposeAsyncImage(
                model = it,
                contentDescription = "post or review img",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(10.dp)
                    .size(75.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator()
                } else {
                    SubcomposeAsyncImageContent()
                }
            }
        }
        item {
            OutlinedIconButton(
                shape = RoundedCornerShape(15.dp),
                onClick = { onAddImgClick() },
                modifier = Modifier
                    .size(95.dp)
                    .padding(10.dp)
            ) {
                Icon(FeatherIcons.Plus, contentDescription = "Add image")
            }
        }
    }
}

@Composable
private fun EnterTitle(
    modifier: Modifier,
    titleValue: String,
    onValueChange: (String) -> Unit,
    titleIsEmpty: Boolean,
    showError: Boolean,
) =
    EnterText(
        placeholder = EnterTitle,
        modifier = modifier,
        value = titleValue,
        onValueChange = onValueChange,
        isError = titleIsEmpty,
        supportingText = {
            if (showError) {
                AnimatedText(visible = titleIsEmpty, text = TitleEmptyError)
            }
        },
        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    )


@Composable
private fun PictureEmojiAndSend(
    onSelectImgClick: () -> Unit,
    onSendClick: () -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    FeatherIcons.Smile,
                    contentDescription = "Picture Action"
                )
            }
            IconButton(
                onClick = { onSelectImgClick() }
            ) {
                Icon(
                    FeatherIcons.Image,
                    contentDescription = "Emoji Action",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSendClick() },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Send, "Send Content")
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    )
}

@Preview(showBackground = true)
@Composable
fun AddContentPreview() {
    AddContent(isPost = true, modifier = Modifier.padding(5.dp), viewModel(), viewModel())
}