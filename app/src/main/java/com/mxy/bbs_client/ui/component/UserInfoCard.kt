package com.mxy.bbs_client.ui.component

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
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
import coil.compose.AsyncImage
import com.mxy.bbs_client.program.ProgramState
import com.mxy.bbs_client.program.viewmodel.*
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit
import compose.icons.feathericons.LogOut
import compose.icons.feathericons.Send
import compose.icons.feathericons.Star
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

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
private const val PersonalSignMaxLen = 10
private const val PersonalSignTooLongError = "个人签名长度不能超过$PersonalSignMaxLen"
private const val NicknameTooLongError = "昵称长度不能超过$NicknameMaxLen"
private const val ModifySuccess = "更改成功"


@RequiresApi(Build.VERSION_CODES.O)
private fun applyChanges(
    context: Context,
    username: String,
    newNickname: String,
    newPersonalSign: String,
    newAvtarUri: Uri?,
    mineScreenViewModel: MineScreenViewModel
) {
    if (newNickname.isEmpty()) {
        Toast.makeText(context, NicknameEmptyError, Toast.LENGTH_SHORT).show()
        return
    }
    if (newNickname.length > NicknameMaxLen) {
        Toast.makeText(context, NicknameTooLongError, Toast.LENGTH_SHORT).show()
        return
    }
    if (newPersonalSign.length > PersonalSignMaxLen) {
        Toast.makeText(context, PersonalSignTooLongError, Toast.LENGTH_SHORT).show()
        return
    }
    with(Utility.IOCoroutineScope) {
        launch {
            val avatarFile = if (newAvtarUri != null) {
                val inputStream = context.contentResolver.openInputStream(newAvtarUri)
                val avatarTmpFile = withContext(Dispatchers.IO) {
                    File.createTempFile("tmpAvatar", null)
                }
                FileUtils.copyInputStreamToFile(inputStream, avatarTmpFile)
                avatarTmpFile
            } else {
                val avatarTmpFile =
                    withContext(Dispatchers.IO) {
                        File.createTempFile("tmpAvatar", null)
                    }
                val avatarUrl =
                    Client.getUserInfo(username).userInfo!!.avatarUrl
                FileUtils.copyURLToFile(URL(avatarUrl as String?), avatarTmpFile)
                avatarTmpFile
            }
            val userInfoResponse = Client.updateUserInfo(
                username,
                newNickname,
                newPersonalSign,
                avatarFile
            )
            with(Utility.UICoroutineScope) {
                launch {
                    if (userInfoResponse.success != null && userInfoResponse.success) {
                        Toast.makeText(context, ModifySuccess, Toast.LENGTH_SHORT).show()
                        mineScreenViewModel.refresh(username)
                    }
                }
            }
        }
    }
}

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
    userInfoViewModel: UserInfoViewModel,
    mineScreenViewModel: MineScreenViewModel,
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    visible: Boolean
) {
    val userInfoState by userInfoViewModel.userInfoState.collectAsState()
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
                avatarUrl = userInfoState.avatarUrl!!,
                nickname = userInfoState.nickname!!,
                personalSign = userInfoState.personalSign!!,
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
                    postIds = userInfoState.myPosts,
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
                    postIds = userInfoState.myCollections,
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
                    avatarUrl = userInfoState.avatarUrl!!,
                    mineScreenViewModel = mineScreenViewModel,
                    username = userInfoViewModel.username
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
    AsyncImage(
        model = avatarUrl,
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
    )
}

@Composable
private fun UserAvatar(
    uri: Uri?,
    avatarUrl: Any,
    modifier: Modifier
) {
    AsyncImage(
        model = uri ?: avatarUrl,
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
    )
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
    postIds: List<String>,
    modifier: Modifier,
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    Column(modifier = modifier) {
        for (postId in postIds) {
            PostThumbnail(
                modifier = Modifier.padding(10.dp),
                postThumbnailViewModel = PostThumbnailViewModel(postId),
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
    mineScreenViewModel: MineScreenViewModel,
    username: String
) {
    val context = LocalContext.current
    val newNicknameState = remember {
        mutableStateOf("")
    }
    val newPersonalSignState = remember {
        mutableStateOf("")
    }
    val avtarUriState = remember {
        mutableStateOf<Uri?>(null)
    }
    val avatarPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            avtarUriState.value = it
            ProgramState.UserInfoState.newAvatar = it
        }
    Column(modifier = modifier) {
        UserAvatar(
            uri = avtarUriState.value,
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
            value = newNicknameState.value,
            onValueChange = {
                newNicknameState.value = it
                ProgramState.UserInfoState.newNickname = it
            }
        )
        //新签名
        EnterText(
            placeholder = InputNewPersonalSign,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = CenterHorizontally),
            value = newPersonalSignState.value,
            onValueChange = {
                newPersonalSignState.value = it
                ProgramState.UserInfoState.newPersonalSign = it
            }
        )
        //提交按钮
        Button(
            modifier = Modifier
                .align(alignment = CenterHorizontally)
                .padding(5.dp),
            onClick = {
                applyChanges(
                    context,
                    username,
                    newNicknameState.value,
                    newPersonalSignState.value,
                    avtarUriState.value,
                    mineScreenViewModel
                )
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
    postThumbnailViewModel: PostThumbnailViewModel
) {
    val postState by postThumbnailViewModel.postState.collectAsState()
    val userInfoState by postThumbnailViewModel.userInfoState.collectAsState()
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                appViewModel.changeScreenTo(0)
                homeScreenViewModel.openPost(postState.id!!)
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row {
            Column {
                AsyncImage(
                    model = userInfoState.avatarUrl,
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .align(alignment = CenterHorizontally)
                )
                Text(
                    text = userInfoState.nickname!!,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(alignment = CenterHorizontally)
                )
            }
            Row {
                if (postState.images.isNotEmpty()) {
                    AsyncImage(
                        model = postState.images[0],
                        contentDescription = "Post Img",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .align(alignment = CenterVertically)
                    )
                }
                Text(
                    text = postState.title!!,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(alignment = CenterVertically)
                )
            }

        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}