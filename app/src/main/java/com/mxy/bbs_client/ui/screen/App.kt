package com.mxy.bbs_client.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.ui.component.AddContent
import com.mxy.bbs_client.ui.component.BottomNavigation
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus
import compose.icons.feathericons.RefreshCcw
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun App(
    appViewModel: AppViewModel = viewModel(),
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    mineScreenViewModel: MineScreenViewModel = viewModel()
) {
    val appState by appViewModel.appState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded })
    ModalBottomSheetLayout(
        sheetContent = {
            if (appState.currentScreen == 0) {
                AddContent(
                    isPost = true,
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        },
        sheetState = sheetState
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigation(appState, appViewModel)
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = appState.currentScreen == 0,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingActionButton(onClick = {
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }) {
                        Icon(FeatherIcons.Plus, contentDescription = "Refresh")
                    }
                }
            }
        ) {
            AnimatedVisibility(
                visible = appState.currentScreen == 0,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it }
            ) {
                HomeScreen(homeScreenViewModel)
            }
            AnimatedVisibility(
                visible = appState.currentScreen == 1,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }
            ) {
                MineScreen(
                    mineScreenViewModel = mineScreenViewModel,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}