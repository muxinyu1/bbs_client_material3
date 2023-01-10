package com.mxy.bbs_client.ui.component

import androidx.compose.animation.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AnimatedText(visible: Boolean, text: String) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    )
    {
        Text(text = text)
    }
}