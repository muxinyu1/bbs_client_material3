package com.mxy.bbs_client.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.program.viewmodel.UserInfoViewModel
import com.mxy.bbs_client.ui.component.NotLoginCard
import com.mxy.bbs_client.ui.component.UserInfoCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MineScreen(
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    mineScreenViewModel: MineScreenViewModel,
    modifier: Modifier
) {
    val mineScreenState by mineScreenViewModel.mineScreenState.collectAsState()
    val userInfoState = mineScreenState.userInfoState
    if (mineScreenState.login) {
        UserInfoCard(
            modifier = modifier,
            mineScreenViewModel = mineScreenViewModel,
            visible = mineScreenState.login,
            homeScreenViewModel = homeScreenViewModel,
            appViewModel = appViewModel,
            //登录后userInfoState不可能是null
            userInfoState = mineScreenState.userInfoState
        )
    }
    NotLoginCard(
        modifier = modifier,
        userInfoState = userInfoState,
        mineScreenViewModel =  mineScreenViewModel,
        visible = !mineScreenState.login
    )
}