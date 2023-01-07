package com.mxy.bbs_client.ui.component

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import com.mxy.bbs_client.program.state.MineScreenState
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
private const val UsernameAlreadyExistError = "用户名已存在"
private const val WrongPasswordError = "用户名或密码不正确"
private const val ConfirmPasswordError = "两次输入的密码不一致"
private const val SignUpSuccess = "注册成功"
private const val LoginSuccess = "登录成功"

private fun signUp(
    mineScreenViewModel: MineScreenViewModel,
    username: String,
    password: String,
    confirmPassword: String,
    context: Context
) {
    if (username.isEmpty()) {
        Toast.makeText(context, UsernameEmptyError, Toast.LENGTH_SHORT).show()
        return
    }
    if (password != confirmPassword) {
        Toast.makeText(context, ConfirmPasswordError, Toast.LENGTH_SHORT).show()
        return
    }
    if (password.isEmpty()) {
        Toast.makeText(context, PasswordEmptyError, Toast.LENGTH_SHORT).show()
    }
    with(Utility.IOCoroutineScope) {
        launch {
            val userResponse = Client.addUser(User(username, password))
            if (!userResponse.success!!) {
                //用户名已存在
                with(Utility.UICoroutineScope) {
                    launch {
                        Toast.makeText(context, UsernameAlreadyExistError, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                //注册成功
                with(Utility.UICoroutineScope) {
                    launch {
                        Toast.makeText(context, SignUpSuccess, Toast.LENGTH_SHORT).show()
                    }
                }
                //自动登录
                mineScreenViewModel.loginSuccessfully(username)
            }
        }
    }
}


private fun login(
    mineScreenViewModel: MineScreenViewModel,
    username: String,
    password: String,
    context: Context
) {
    if (username.isEmpty()) {
        Toast.makeText(context, UsernameEmptyError, Toast.LENGTH_SHORT).show()
        return
    }
    if (password.isEmpty()) {
        Toast.makeText(context, PasswordEmptyError, Toast.LENGTH_SHORT).show()
        return
    }
    with(Utility.IOCoroutineScope) {
        launch {
            val userResponse = Client.getUser(username)
            if (!userResponse.success!!) {
                //用户名不存在
                with(Utility.UICoroutineScope) {
                    launch {
                        Toast.makeText(context, WrongPasswordError, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                if (userResponse.user!!.password!! != password) {
                    //密码不正确
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, WrongPasswordError, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    //密码正确
                    with(Utility.UICoroutineScope) {
                        launch {
                            Toast.makeText(context, LoginSuccess, Toast.LENGTH_SHORT).show()
                        }
                    }
                    mineScreenViewModel.loginSuccessfully(username)
                }
            }
        }
    }
}

@Composable
fun NotLoginCard(
    modifier: Modifier,
    userInfoViewModel: UserInfoViewModel,
    mineScreenViewModel: MineScreenViewModel,
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
            //未登录用户头像和昵称
            UserAvatarNicknameAndSign(
                avatarUrl = userInfoState.avatarUrl!!,
                nickname = userInfoState.nickname!!,
                personalSign = userInfoState.personalSign!!,
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
    val context = LocalContext.current
    Column(modifier = modifier.padding(10.dp)) {
        EnterText(placeholder = Username,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = username,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
            onValueChange = {
                username = it
            })
        EnterText(
            visualTransformation = PasswordVisualTransformation(),
            placeholder = Password,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = password,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            onValueChange = {
                password = it
            })
        EnterText(
            placeholder = ConfirmPassword,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = confirmPassword,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            onValueChange = {
                confirmPassword = it
            })
        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(5.dp),
            onClick = {
                signUp(mineScreenViewModel, username, password, confirmPassword, context)
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
    val context = LocalContext.current
    Column(modifier = modifier) {
        EnterText(placeholder = Username,
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = username,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
            onValueChange = {
                username = it
            })
        EnterText(
            placeholder = Password,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.CenterHorizontally),
            value = password,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            onValueChange = {
                password = it
            })
        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(5.dp),
            onClick = {
                login(mineScreenViewModel, username, password, context)
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