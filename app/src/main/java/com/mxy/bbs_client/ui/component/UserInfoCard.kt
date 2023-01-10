package com.mxy.bbs_client.ui.component

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mxy.bbs_client.program.ProgramState
import com.mxy.bbs_client.program.state.PostState
import com.mxy.bbs_client.program.state.UserInfoState
import com.mxy.bbs_client.program.viewmodel.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit
import compose.icons.feathericons.LogOut
import compose.icons.feathericons.Send
import compose.icons.feathericons.Star

private const val MyCollections = "我的收藏"
private const val MyPosts = "我的帖子"
private const val EditInfo = "编辑信息"
private const val LogOut = "退出登录"
private const val ConfirmLogOut = "确认"
private const val InputNewNickname = "新昵称"
private const val InputNewPersonalSign = "新个人签名"
private const val SubmitChanges = "提交更改"
private const val PickAnImage = "image/*"
private const val NicknameEmptyError = "昵称不能为空"
private const val NicknameMaxLen = 15
private const val PersonalSignMaxLen = 12
private const val NicknameTooLongError = "昵称长度不能超过$NicknameMaxLen"
private const val personalSignTooLongError = "个人签名长度不能超过$PersonalSignMaxLen"


@Composable
fun UserAvatarNicknameAndSign(
    avatarUrl: Any,
    nickname: String,
    personalSign: String,
    modifier: Modifier
) {
    Row(modifier = modifier) {
        UserAvatar(
            avatarUrl = avatarUrl,
            modifier = Modifier
                .align(alignment = CenterVertically)
                .size(100.dp)
        )
        Column {
            UserNickname(
                modifier = Modifier.padding(10.dp),
                nickname = nickname
            )
            PersonalSign(sign = personalSign, modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
private fun PersonalSign(
    sign: String,
    modifier: Modifier
) {
    Text(
        text = sign,
        modifier = modifier,
        fontSize = 16.sp,
        fontWeight = FontWeight.Light
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserInfoCard(
    modifier: Modifier,
    userInfoState: UserInfoState,
    mineScreenViewModel: MineScreenViewModel,
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    visible: Boolean
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Column(modifier = modifier) {
            UserAvatarNicknameAndSign(
                avatarUrl = userInfoState.avatarUrl,
                nickname = userInfoState.nickname,
                personalSign = userInfoState.personalSign,
                modifier = Modifier.padding(10.dp)
            )
            //我的帖子
            ExpandableCard(
                modifier = Modifier.padding(10.dp),
                header = {
                    Header(imageVector = FeatherIcons.Send, text = MyPosts)
                }
            ) {
                UserPosts(
                    postStates = userInfoState.myPosts,
                    modifier = Modifier.padding(5.dp),
                    appViewModel = appViewModel,
                    homeScreenViewModel = homeScreenViewModel
                )
            }
            //我的收藏
            ExpandableCard(
                modifier = Modifier.padding(10.dp),
                header = {
                    Header(imageVector = FeatherIcons.Star, text = MyCollections)
                }
            ) {
                UserPosts(
                    postStates = userInfoState.myCollections,
                    modifier = Modifier.padding(5.dp),
                    homeScreenViewModel = homeScreenViewModel,
                    appViewModel = appViewModel
                )
            }
            //编辑信息
            ExpandableCard(
                modifier = Modifier.padding(10.dp),
                header = {
                    Header(imageVector = FeatherIcons.Edit, text = EditInfo)
                }
            ) {
                EditUserInfo(
                    modifier = Modifier.padding(10.dp),
                    avatarUrl = userInfoState.avatarUrl,
                    mineScreenViewModel = mineScreenViewModel
                )
            }
            //退出登录
            ExpandableCard(
                modifier = Modifier.padding(10.dp),
                header = {
                    Header(imageVector = FeatherIcons.LogOut, text = LogOut)
                },
                foldedContent = {
                    Button(
                        modifier = Modifier
                            .align(alignment = CenterHorizontally)
                            .padding(10.dp),
                        onClick = {
                            mineScreenViewModel.logout()
                        },
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            text = ConfirmLogOut,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(alignment = CenterVertically)
                        )
                    }

                }
            )

        }
    }
}

@Composable
fun Header(imageVector: ImageVector, text: String) {
    Row {
        Icon(
            imageVector,
            contentDescription = "Edit Info",
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = CenterVertically)
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(alignment = CenterVertically)
        )
    }
}

@Composable
private fun UserAvatar(
    avatarUrl: Any,
    modifier: Modifier
) {
    SubcomposeAsyncImage(
        model = avatarUrl,
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            CircularProgressIndicator()
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}

@Composable
private fun UserAvatar(
    uri: Uri?,
    avatarUrl: Any,
    modifier: Modifier
) {
    SubcomposeAsyncImage(
        model = uri ?: avatarUrl,
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            CircularProgressIndicator()
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}

@Composable
private fun UserNickname(
    modifier: Modifier,
    nickname: String
) {
    Text(
        text = nickname,
        modifier = modifier,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun UserPosts(
    postStates: List<PostState>,
    modifier: Modifier,
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    Column(modifier = modifier) {
        for (postState in postStates) {
            PostThumbnail(
                modifier = Modifier.padding(10.dp),
                postState = postState,
                homeScreenViewModel = homeScreenViewModel,
                appViewModel = appViewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditUserInfo(
    modifier: Modifier,
    avatarUrl: Any,
    mineScreenViewModel: MineScreenViewModel
) {
    val context = LocalContext.current
    var newNickname by remember {
        mutableStateOf("")
    }
    var newPersonalSign by remember {
        mutableStateOf("")
    }
    var newAvtarUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var showError by remember {
        mutableStateOf(false)
    }
    val avatarPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            newAvtarUri = it
            ProgramState.UserInfoState.newAvatar = it
        }
    Column(modifier = modifier) {
        UserAvatar(
            uri = newAvtarUri,
            avatarUrl = avatarUrl,
            modifier = Modifier
                .clip(CircleShape)
                .size(75.dp)
                .padding(5.dp)
                .align(alignment = CenterHorizontally)
                .clickable {
                    avatarPickerLauncher.launch(PickAnImage)
                }
        )
        //新昵称
        EnterText(
            placeholder = InputNewNickname,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = CenterHorizontally),
            value = newNickname,
            onValueChange = {
                newNickname = it
            },
            isError = if (showError) newNickname.length > NicknameMaxLen || newNickname.isBlank() else false,
            supportingText = {
                if (showError) {
                    Column {
                        AnimatedText(visible = newNickname.isBlank(), text = NicknameEmptyError)
                        AnimatedText(
                            visible = newNickname.length > NicknameMaxLen,
                            text = NicknameTooLongError
                        )
                    }
                }
            }
        )
        //新签名
        EnterText(
            placeholder = InputNewPersonalSign,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = CenterHorizontally),
            value = newPersonalSign,
            onValueChange = {
                newPersonalSign = it
            },
            isError = if (showError) newPersonalSign.length > PersonalSignMaxLen else false,
            supportingText = {
                if (showError) {
                    AnimatedText(
                        visible = newPersonalSign.length > PersonalSignMaxLen,
                        text = personalSignTooLongError
                    )
                }
            }
        )
        //提交按钮
        Button(
            modifier = Modifier
                .align(alignment = CenterHorizontally)
                .padding(5.dp),
            onClick = {
                val enabled = newNickname.isNotBlank()
                        && newNickname.length <= NicknameMaxLen
                        && newPersonalSign.length <= PersonalSignMaxLen
                if (!enabled) {
                    showError = true
                } else {
                    mineScreenViewModel.updateUserInfo(
                        context = context,
                        newNickname = newNickname,
                        newPersonalSign = newPersonalSign,
                        newAvtarUri = newAvtarUri
                    )
                }
            },
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = SubmitChanges,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(alignment = CenterVertically)
            )
        }
    }
}

@Composable
fun PostThumbnail(
    modifier: Modifier,
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    postState: PostState
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                appViewModel.changeScreenTo(0)
                homeScreenViewModel.openPost(postState.postId)
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row {
            Column(modifier = Modifier.align(alignment = CenterVertically)) {
                SubcomposeAsyncImage(
                    model = postState.avatarUrl,
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .align(alignment = CenterHorizontally)
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator()
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
                Text(
                    text = postState.nickname,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(5.dp).align(alignment = CenterHorizontally)
                )
            }
            Row(modifier = Modifier.align(alignment = CenterVertically)) {
                if (postState.images.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = postState.images[0],
                        contentDescription = "Post Img",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .align(alignment = CenterVertically)
                    ) {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                            CircularProgressIndicator()
                        } else {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
                Text(
                    text = postState.title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(5.dp).align(alignment = CenterVertically)
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}