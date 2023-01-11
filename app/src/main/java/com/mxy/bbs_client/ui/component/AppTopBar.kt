package com.mxy.bbs_client.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import compose.icons.FeatherIcons
import compose.icons.LineAwesomeIcons
import compose.icons.TablerIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.lineawesomeicons.Connectdevelop
import compose.icons.lineawesomeicons.UsersSolid
import compose.icons.tablericons.Social

private const val HomeScreenText = "首页"

private const val MineScreenText = "我的"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    index: Int,
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    val appState by appViewModel.appState.collectAsState()
    val homeScreenState by homeScreenViewModel.homeScreenState.collectAsState()
    val tweenSpec = tween<Int>(150)
    val homeFontSize by animateIntAsState(
        targetValue = if (index == 0) 22 else 16,
        animationSpec = tweenSpec
    )
    val mineFontSize by animateIntAsState(
        targetValue = if (index == 1) 22 else 16,
        animationSpec = tweenSpec
    )
    val homeTextIndicatorLen by animateIntAsState(
        targetValue = if (index == 0) 15 else 0,
        animationSpec = tweenSpec
    )
    val mineTextIndicatorLen by animateIntAsState(
        targetValue = if (index == 1) 15 else 0,
        animationSpec = tweenSpec
    )
    CenterAlignedTopAppBar(
        title = {
            Row(modifier = Modifier.animateContentSize()) {
                //"首页"
                Column(
                    modifier = Modifier
                        .align(alignment = CenterVertically)
                        .clickable { appViewModel.changeScreenTo(0) }
                ) {
                    Text(
                        modifier = Modifier.align(alignment = CenterHorizontally),
                        text = HomeScreenText,
                        fontSize = homeFontSize.sp,
                        fontWeight = if (index == 0) FontWeight.Bold else null
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .height(3.dp)
                            .width(homeTextIndicatorLen.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .align(alignment = CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                //"我的"
                Column(
                    modifier = Modifier
                        .align(alignment = CenterVertically)
                        .clickable { appViewModel.changeScreenTo(1) }
                ) {
                    Text(
                        modifier = Modifier.align(alignment = CenterHorizontally),
                        text = MineScreenText,
                        fontSize = mineFontSize.sp,
                        fontWeight = if (index == 1) FontWeight.Bold else null
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .height(3.dp)
                            .width(mineTextIndicatorLen.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .align(alignment = CenterHorizontally)
                    )
                }
            }
        },
        navigationIcon = {
            val backVisibility = homeScreenState.openedPost != null && appState.currentScreen == 0
            AnimatedVisibility(
                visible = backVisibility,
                enter = slideInHorizontally{-it},
                exit = slideOutHorizontally{-it}
            ) {
                IconButton(onClick = { homeScreenViewModel.closePost() }) {
                    Icon(FeatherIcons.ArrowLeft, contentDescription = "Back to Home")
                }
            }
            AnimatedVisibility(
                visible = !backVisibility,
                enter = slideInHorizontally{-it},
                exit = slideOutHorizontally{-it}
            ) {
                IconButton(onClick = {  }) {
                    Icon(LineAwesomeIcons.Connectdevelop, contentDescription = "App Icon")
                }
            }
        },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}