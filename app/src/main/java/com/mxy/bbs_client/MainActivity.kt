@file:OptIn(ExperimentalMaterial3Api::class)

package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.ui.component.BottomNavigation
import com.mxy.bbs_client.ui.component.PostCardList
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Home
import compose.icons.feathericons.RefreshCcw

class MainActivity : ComponentActivity() {
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
                    PostCardList()
                }
            }
        }
    }
}