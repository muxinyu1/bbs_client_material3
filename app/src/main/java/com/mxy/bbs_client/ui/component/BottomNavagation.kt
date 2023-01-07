package com.mxy.bbs_client.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import compose.icons.FeatherIcons
import compose.icons.feathericons.Home
import compose.icons.feathericons.User

private val BottomItems = listOf("首页", "我的")
private val BottomIcons = listOf(FeatherIcons.Home, FeatherIcons.User)

@Composable
fun BottomNavigation() {
    val selectedItem = remember {
        mutableStateOf(0)
    }
    NavigationBar {
        BottomItems.forEachIndexed{ index, item ->
            NavigationBarItem(
                selected = selectedItem.value == index,
                onClick = { selectedItem.value = index },
                icon = { Icon(BottomIcons[index], contentDescription = item)},
                label = { Text(text = item)}
            )
        }
    }
}