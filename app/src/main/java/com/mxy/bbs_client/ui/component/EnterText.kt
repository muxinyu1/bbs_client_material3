package com.mxy.bbs_client.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterText(
    placeholder: String,
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        modifier = modifier.fillMaxWidth(),
        //TODO:输入框的颜色主题
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        label = { Text(text = placeholder) },
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = placeholder) }
    )
}

@Preview(showBackground = true)
@Composable
fun EnterTextPreview() {
    val value = remember {
        mutableStateOf("")
    }
    //EnterText(placeholder = "用户名", modifier = Modifier, value = value.value)
}