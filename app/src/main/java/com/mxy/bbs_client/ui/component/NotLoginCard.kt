package com.mxy.bbs_client.ui.component

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mxy.bbs_client.entity.user.User
import com.mxy.bbs_client.program.state.UserInfoState
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.program.viewmodel.UserInfoViewModel
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.LogIn
import compose.icons.feathericons.PlusCircle
import kotlinx.coroutines.launch

private const val LoginText = "登录"
private const val RegisterText = "注册"
private const val Username = "用户名"
private const val Password = "密码"
private const val ConfirmPassword = "确认密码"
private const val UsernameEmptyError = "用户名不能为空"
private const val PasswordEmptyError = "密码不能为空"
private const val ConfirmPasswordError = "两次输入的密码不一致"

@Composable
fun NotLoginCard(
    modifier: Modifier,
    userInfoState: UserInfoState,
    mineScreenViewModel: MineScreenViewModel,
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
            //未登录用户头像和昵称
            UserAvatarNicknameAndSign(
                avatarUrl = userInfoState.avatarUrl,
                nickname = userInfoState.nickname,
                personalSign = userInfoState.personalSign,
                modifier = Modifier.padding(10.dp)
            )
            //登录
            ExpandableCard(
                modifier = Modifier.padding(10.dp),
                header = {
                    Header(imageVector = FeatherIcons.LogIn, text = LoginText)
                },
                foldedContent = {
                    Login(modifier = Modifier.padding(10.dp), mineScreenViewModel)
                }
            )
            //注册
            ExpandableCard(
                modifier = Modifier.padding(10.dp),
                header = {
                    Header(imageVector = FeatherIcons.PlusCircle, text = RegisterText)
                },
                foldedContent = {
                    SignUp(modifier = Modifier.padding(10.dp), mineScreenViewModel)
                }
            )
        }
    }
}

@Composable
private fun SignUp(modifier: Modifier, mineScreenViewModel: MineScreenViewModel) {
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirmPassword by remember {
        mutableStateOf("")
    }
    var showError by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Column(modifier = modifier.padding(10.dp)) {
        EnterText(
            placeholder = Username,
            isError = if (showError) username.isBlank() else false,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = username,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
            onValueChange = {
                username = it
            },
            supportingText = {
                if (showError) AnimatedText(visible = username.isBlank(), text = UsernameEmptyError)
            }
        )
        EnterText(
            visualTransformation = PasswordVisualTransformation(),
            isError = if (showError) password.isBlank() else false,
            placeholder = Password,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = password,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            onValueChange = {
                password = it
            },
            supportingText = {
                if (showError) AnimatedText(visible = password.isBlank(), text = PasswordEmptyError)
            }
        )
        EnterText(
            placeholder = ConfirmPassword,
            visualTransformation = PasswordVisualTransformation(),
            isError = if (showError) password != confirmPassword else false,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = confirmPassword,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            onValueChange = {
                confirmPassword = it
            },
            supportingText = {
                if (showError) AnimatedText(
                    visible = password != confirmPassword,
                    text = ConfirmPasswordError
                )
            }
        )
        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(5.dp),
            onClick = {
                if (!(username.isNotBlank() && password == confirmPassword && password.isNotBlank())) {
                    showError = true
                } else {
                    mineScreenViewModel.signUp(username, password, context)
                }
            },
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = RegisterText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun Login(modifier: Modifier, mineScreenViewModel: MineScreenViewModel) {
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var showError by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Column(modifier = modifier) {
        EnterText(
            isError = if (showError) username.isBlank() else false,
            placeholder = Username,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = username,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
            onValueChange = {
                username = it
            },
            supportingText = {
                if (showError) AnimatedText(visible = username.isBlank(), text = UsernameEmptyError)
            }
        )
        EnterText(
            isError = if (showError) password.isBlank() else false,
            placeholder = Password,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = password,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            onValueChange = {
                password = it
            },
            supportingText = {
                if (showError) AnimatedText(visible = password.isBlank(), text = PasswordEmptyError)
            }
        )
        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(5.dp),
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    mineScreenViewModel.login(
                        username = username,
                        password = password,
                        context = context
                    )
                } else {
                    showError = true
                }
            },
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = LoginText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
        }
    }
}