package com.mxy.bbs_client.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val EnterUsername = "用户名"
private const val EnterPassword = "密码"

@Composable
fun LoginAndRegister(
    userNameStr: String,
    passwordStr: String,
    loginButtonStr: String,
    registerButtonStr: String,
    enterTextFocusColor: Color,
    buttonTxtColor: Color,
    loginButtonBrush: Brush,
    registerButtonBrush: Brush,
    modifier: Modifier,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    userNameVal: MutableState<String>,
    passwordVal: MutableState<String>
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
//        EnterText(
//            placeholder = userNameStr,
//            focusColor = enterTextFocusColor,
//            modifier = Modifier.padding(5.dp),
//            value = userNameVal
//        )
//        EnterText(placeholder = EnterUsername, modifier = Modifier.padding(5.dp), value = , onValueChange = )
//        EnterText(
//            placeholder = passwordStr,
//            focusColor = enterTextFocusColor,
//            modifier = Modifier.padding(5.dp),
//            value = passwordVal
//        )
        SimpleButton(
            text = loginButtonStr,
            textColor = buttonTxtColor,
            gradient = loginButtonBrush,
            onClick = onLoginClick,
            modifier = Modifier.padding(5.dp)
        )
        SimpleButton(
            text = registerButtonStr,
            textColor = buttonTxtColor,
            gradient = registerButtonBrush,
            onClick = onRegisterClick,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginAndRegisterPreview() {
    val username = remember {
        mutableStateOf("")
    }
    val psw = remember {
        mutableStateOf("")
    }
    LoginAndRegister(
        userNameStr = "用户名",
        passwordStr = "密码",
        loginButtonStr = "             登录             ",
        registerButtonStr = "还没账号？注册一个！",
        enterTextFocusColor = Color.Black,
        buttonTxtColor = Color.Black,
        loginButtonBrush = Brush.horizontalGradient(listOf(Color.White, Color.White)),
        registerButtonBrush = Brush.horizontalGradient(listOf(Color.White, Color.White)),
        modifier = Modifier,
        onLoginClick = { },
        onRegisterClick = {},
        userNameVal = username,
        passwordVal = psw
    )
}