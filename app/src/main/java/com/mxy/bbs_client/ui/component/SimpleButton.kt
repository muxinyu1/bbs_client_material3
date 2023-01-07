package com.mxy.bbs_client.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleButton(
    text: String,
    textColor: Color,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
        modifier = modifier
    )
    {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = textColor)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SimpleButtonPreview() {
    SimpleButton(
        text = "登录",
        textColor = Color.White,
        gradient = Brush.horizontalGradient(colors = listOf(Color.Black, Color.White)),
        onClick = {},
        modifier = Modifier
    )
}