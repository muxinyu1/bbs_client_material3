package com.mxy.bbs_client.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.ui.component.AddContent
import com.mxy.bbs_client.ui.component.AppTopBar
import com.mxy.bbs_client.ui.component.BottomNavigation
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus
import compose.icons.feathericons.RefreshCw
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun App(
    appViewModel: AppViewModel = viewModel(),
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    mineScreenViewModel: MineScreenViewModel = viewModel()
) {
    val appState by appViewModel.appState.collectAsState()
    val homeScreenState by homeScreenViewModel.homeScreenState.collectAsState()
    val isRefreshing by mineScreenViewModel.isRefreshing.collectAsState()
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
        //val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            //modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppTopBar(
                    index = appState.currentScreen,
                    appViewModel,
                    mineScreenViewModel,
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
                        Icon(FeatherIcons.Plus, contentDescription = "add content")
                    }
                }
                AnimatedVisibility(
                    visible = appState.currentScreen == 1,
                    enter = slideInHorizontally { (3 * it) / 2 },
                    exit = slideOutHorizontally { (3 * it) / 2 }
                ) {
                    FloatingActionButton(onClick = {
                        mineScreenViewModel.refresh()
                    }) {
                        var currentRotation by remember { mutableStateOf(0f) }
                        val rotation = remember { Animatable(currentRotation) }
                        LaunchedEffect(isRefreshing) {
                            if (isRefreshing) {
                                rotation.animateTo(
                                    targetValue = currentRotation + 360f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(3000, easing = LinearEasing)
                                    )
                                ) {
                                    currentRotation = value
                                }
                            } else {
                                if (currentRotation > 0f) {
                                    rotation.animateTo(
                                        targetValue = currentRotation + 50,
                                        animationSpec = tween(
                                            durationMillis = 1250,
                                            easing = LinearOutSlowInEasing
                                        )
                                    ) {
                                        currentRotation = value
                                    }
                                }
                            }
                        }
                        Icon(
                            FeatherIcons.RefreshCw,
                            contentDescription = "Refresh",
                            modifier = Modifier.rotate(currentRotation)
                        )
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
                HomeScreen(
                    homeScreenViewModel = homeScreenViewModel,
                    mineScreenViewModel = mineScreenViewModel
                )
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