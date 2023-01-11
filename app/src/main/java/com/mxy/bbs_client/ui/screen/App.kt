package com.mxy.bbs_client.ui.screen

import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.ui.component.AddContent
import com.mxy.bbs_client.ui.component.AppTopBar
import com.mxy.bbs_client.ui.component.BottomNavigation
import com.mxy.bbs_client.ui.component.vibrator
import compose.icons.FeatherIcons
import compose.icons.feathericons.Heart
import compose.icons.feathericons.Plus
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun App(
    appViewModel: AppViewModel = viewModel(),
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    mineScreenViewModel: MineScreenViewModel = viewModel()
) {
    val appState by appViewModel.appState.collectAsState()
    val homeScreenState by homeScreenViewModel.homeScreenState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetIsOpen by mineScreenViewModel.bottomSheetState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    ModalBottomSheetLayout(
        modifier = Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onHorizontalDrag = { change, dragAmount ->
                    //TODO:水平滑动切换Screen
                }
            )
        },
        sheetShape = RoundedCornerShape(20.dp),
        sheetContent = {
            if (appState.currentScreen == 0) {
                if (homeScreenState.openedPost == null) {
                    AddContent(
                        isPost = true,
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)),
                        mineScreenViewModel = mineScreenViewModel,
                        homeScreenViewModel = homeScreenViewModel
                    )
                } else {
                    AddContent(
                        isPost = false,
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(
                                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            ),
                        mineScreenViewModel = mineScreenViewModel,
                        targetPost = homeScreenState.openedPost,
                        homeScreenViewModel = homeScreenViewModel
                    )
                }
            } else {
                Text(text = "")
            }
        },
        sheetState = if (bottomSheetIsOpen.first()) sheetState.apply {
            coroutineScope.launch {
                sheetState.show()
            }
        } else {
            sheetState.apply {
                coroutineScope.launch {
                    sheetState.hide()
                }
            }
        }
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            //modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppTopBar(
                    index = appState.currentScreen,
                    appViewModel,
                    homeScreenViewModel,
                    scrollBehavior = null
                )
            },
            bottomBar = {
                BottomNavigation(appState, appViewModel)
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = appState.currentScreen == 0,
                    enter = slideInHorizontally { (3 * it) / 2 },
                    exit = slideOutHorizontally { (3 * it) / 2 }
                ) {
                    FloatingActionButton(onClick = {
                        mineScreenViewModel.openBottomSheet()
                    }) {
                        Icon(FeatherIcons.Plus, contentDescription = "Refresh")
                    }
                }
            }
        ) { paddingValues ->
            AnimatedVisibility(
                visible = appState.currentScreen == 0,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
                modifier = Modifier.padding(paddingValues)
            ) {
                HomeScreen(homeScreenViewModel)
            }
            AnimatedVisibility(
                visible = appState.currentScreen == 1,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
                modifier = Modifier.padding(paddingValues)
            ) {
                MineScreen(
                    mineScreenViewModel = mineScreenViewModel,
                    modifier = Modifier.padding(5.dp),
                    appViewModel = appViewModel,
                    homeScreenViewModel = homeScreenViewModel
                )
            }
        }
    }
}