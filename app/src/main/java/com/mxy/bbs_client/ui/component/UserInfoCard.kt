package com.mxy.bbs_client.ui.component

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mxy.bbs_client.program.ProgramState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit
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
private fun applyChanges(context: Context) {
    if (ProgramState.UserInfoState.newNickname.isEmpty()) {
        Toast.makeText(context, NicknameEmptyError, Toast.LENGTH_SHORT).show()
        return
    }
    if (ProgramState.UserInfoState.newNickname.length > NicknameMaxLen) {
        Toast.makeText(context, NicknameTooLongError, Toast.LENGTH_SHORT).show()
        return
    }
    if (ProgramState.UserInfoState.newPersonalSign.length > PersonalSignMaxLen) {
        Toast.makeText(context, PersonalSignTooLongError, Toast.LENGTH_SHORT).show()
        return
    }
    with(Utility.IOCoroutineScope) {
        launch {
            val avatarUri = ProgramState.UserInfoState.newAvatar
            val avatarFile = if (avatarUri != null) {
                val inputStream = context.contentResolver.openInputStream(avatarUri)
                val avatarTmpFile = withContext(Dispatchers.IO) {
                    File.createTempFile("tmpAvatar", null)
                }
                FileUtils.copyInputStreamToFile(inputStream, avatarTmpFile)
                avatarTmpFile
            } else {
                val avatarTmpFile =
                    withContext(Dispatchers.IO) {
                        File.createTempFile("tmpAvatar", "png")
                    }
                val avatarUrl =
                    Client.getUserInfo(ProgramState.UserInfoState.username).userInfo!!.avatarUrl
                FileUtils.copyURLToFile(URL(avatarUrl), avatarTmpFile)
                avatarTmpFile
            }
            Log.d(
                "applyChanges",
                "newNickName = ${ProgramState.UserInfoState.newNickname}, newPersonalSign = ${ProgramState.UserInfoState.newPersonalSign}, avatar = ${avatarFile.absolutePath}"
            )
            val userInfoResponse = Client.updateUserInfo(
                ProgramState.UserInfoState.username,
                ProgramState.UserInfoState.newNickname,
                ProgramState.UserInfoState.newPersonalSign,
                avatarFile
            )
            with(Utility.UICoroutineScope) {
                launch {
                    if (userInfoResponse.success != null && userInfoResponse.success) {
                        Toast.makeText(context, ModifySuccess, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserInfoCard(
    modifier: Modifier,
    username: String
) {
    val userInfoState = remember {
        mutableStateOf(DefaultUserInfo)
    }
    Column(modifier = modifier) {
        UserAvatar(
            avatarUrl = userInfoState.value.avatarUrl!!,
            modifier = Modifier
                .align(alignment = CenterHorizontally)
                .size(100.dp)
        )
        UserNickname(
            modifier = Modifier.align(alignment = CenterHorizontally),
            nickname = userInfoState.value.nickname!!
        )
        //我的帖子
        ExpandableCard(
            modifier = Modifier.padding(10.dp),
            header = {
                Row {
                    Icon(
                        FeatherIcons.Send,
                        contentDescription = "My Posts",
                        modifier = Modifier
                            .padding(5.dp)
                            .align(alignment = CenterVertically)
                    )
                    Text(
                        text = MyPosts,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(alignment = CenterVertically)
                    )
                }
            },
            foldedContent = {
                UserPosts(postIds = userInfoState.value.myPosts, modifier = Modifier.padding(5.dp))
            },
            arrowColor = Color.Black
        )
        //我的收藏
        ExpandableCard(
            modifier = Modifier.padding(10.dp),
            header = {
                Row {
                    Icon(
                        FeatherIcons.Star,
                        contentDescription = "My Fav",
                        modifier = Modifier
                            .padding(5.dp)
                            .align(alignment = CenterVertically)
                    )
                    Text(
                        text = MyCollections,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(alignment = CenterVertically)
                    )
                }
            },
            foldedContent = {
                UserPosts(
                    postIds = userInfoState.value.myCollections,
                    modifier = Modifier.padding(5.dp)
                )
            },
            arrowColor = Color.Black
        )
        //编辑信息
        ExpandableCard(
            modifier = Modifier.padding(10.dp),
            header = {
                Row {
                    Icon(
                        FeatherIcons.Edit,
                        contentDescription = "Edit Info",
                        modifier = Modifier
                            .padding(5.dp)
                            .align(alignment = CenterVertically)
                    )
                    Text(
                        text = EditInfo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(alignment = CenterVertically)
                    )
                }
            },
            foldedContent = {
                EditUserInfo(
                    modifier = Modifier.padding(10.dp),
                    avatarUrl = userInfoState.value.avatarUrl!!
                )
            },
            arrowColor = Color.Black
        )
    }
    with(Utility.IOCoroutineScope) {
        launch {
            userInfoState.value = Client.getUserInfo(username).userInfo!!
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun UserInfoCardPreview() {
    UserInfoCard(modifier = Modifier, username = "真想")
}

@Composable
private fun UserAvatar(
    avatarUrl: String,
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
    avatarUrl: String,
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
private fun UserPosts(postIds: List<String>, modifier: Modifier) {
    Column(modifier = modifier) {
        for (postId in postIds) {
            PostThumbnail(modifier = Modifier.padding(10.dp), postId = postId)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditUserInfo(modifier: Modifier, avatarUrl: String) {
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
                applyChanges(context)
            },
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
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
    postId: String
) {
    val postState = remember {
        mutableStateOf(DefaultPost)
    }
    val userInfoState = remember {
        mutableStateOf(DefaultUserInfo)
    }
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { ProgramState.PostState.openedPost = postId },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row {
            Column {
                AsyncImage(
                    model = userInfoState.value.avatarUrl,
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
                    text = userInfoState.value.nickname!!,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(alignment = CenterHorizontally)
                )
            }
            Row {
                if (postState.value.images.isNotEmpty()) {
                    AsyncImage(
                        model = postState.value.images[0],
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
                    text = postState.value.title!!,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(alignment = CenterVertically)
                )
            }

        }
        Spacer(modifier = Modifier.height(5.dp))

    }
    with(Utility.IOCoroutineScope) {
        launch {
            postState.value = Client.getPost(postId).post!!
            userInfoState.value = Client.getUserInfo(postState.value.owner).userInfo!!
        }
    }
}