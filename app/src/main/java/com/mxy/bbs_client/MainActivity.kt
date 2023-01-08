package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.ui.screen.App
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Bbs_clientTheme {
                App()
            }
        }
    }
}