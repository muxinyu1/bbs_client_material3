@file:OptIn(ExperimentalMaterial3Api::class)

package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.ui.component.BottomNavigation
import com.mxy.bbs_client.ui.screen.MineScreen
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCcw

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Bbs_clientTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigation()
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {

                        }
                        ) {
                            Icon(FeatherIcons.RefreshCcw, contentDescription = "Refresh")
                        }
                    }
                ) {
                    MineScreen(
                        mineScreenViewModel = MineScreenViewModel(),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}