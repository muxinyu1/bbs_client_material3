package com.mxy.bbs_client.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.ui.component.PostCardList

@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel) {
    val homeScreenState by homeScreenViewModel.homeScreenState.collectAsState()
    PostCardList(homeScreenState.postList, modifier = Modifier.padding(10.dp), homeScreenViewModel)
}