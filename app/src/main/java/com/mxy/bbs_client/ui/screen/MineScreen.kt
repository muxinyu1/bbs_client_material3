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

private const val NotLogin = "未登录"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MineScreen(
    appViewModel: AppViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    mineScreenViewModel: MineScreenViewModel,
    modifier: Modifier
) {
    val mineScreenState by mineScreenViewModel.mineScreenState.collectAsState()
    if (mineScreenState.login) {
        UserInfoCard(
            modifier = modifier,
            userInfoViewModel = UserInfoViewModel(mineScreenState.username!!),
            mineScreenViewModel = mineScreenViewModel,
            visible = mineScreenState.login,
            homeScreenViewModel = homeScreenViewModel,
            appViewModel = appViewModel
        )
    }
    NotLoginCard(
        modifier = modifier,
        userInfoViewModel = UserInfoViewModel(NotLogin),
        mineScreenViewModel =  mineScreenViewModel,
        visible = !mineScreenState.login
    )
}