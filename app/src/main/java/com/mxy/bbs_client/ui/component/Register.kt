package com.mxy.bbs_client.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Register(
    enterUserNameStr: String,
    passwordStr: String,
    confirmPasswordStr: String,
    registerButtonStr: String,
    enterTextFocusColor: Color,
    buttonTxtColor: Color,
    registerButtonBrush: Brush,
    modifier: Modifier,
    onRegisterClick: () -> Unit,
    usernameVal: MutableState<String>,
    passwordVal: MutableState<String>,
    confirmPasswordVal: MutableState<String>
) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
//        EnterText(placeholder = enterUserNameStr, focusColor = enterTextFocusColor, modifier = Modifier.padding(5.dp), value = usernameVal)
//        EnterText(placeholder = passwordStr, focusColor = enterTextFocusColor, modifier = Modifier.padding(5.dp), value = passwordVal)
//        EnterText(placeholder = confirmPasswordStr, focusColor = enterTextFocusColor, modifier = Modifier.padding(5.dp), value = confirmPasswordVal)
//        SimpleButton(text = registerButtonStr, textColor = buttonTxtColor, gradient = registerButtonBrush, onClick = onRegisterClick, modifier = Modifier.padding(5.dp))
//    }
}

@Composable
@Preview
fun RegisterPreview() {
    val usernameVal = remember { mutableStateOf("") }
    val passwordVal = remember { mutableStateOf("") }
    val confirmPasswordVal = remember { mutableStateOf("") }
    Register(
        enterUserNameStr = "输入用户名",
        passwordStr = "输入密码",
        confirmPasswordStr = "确认密码",
        registerButtonStr = "          注册          ",
        enterTextFocusColor = Color.Black,
        buttonTxtColor = Color.Black,
        registerButtonBrush = Brush.horizontalGradient(listOf(Color.White, Color.White)),
        modifier = Modifier,
        onRegisterClick = {},
        usernameVal = usernameVal,
        passwordVal = passwordVal,
        confirmPasswordVal = confirmPasswordVal
    )
}