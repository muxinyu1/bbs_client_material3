package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.material3.*
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.program.viewmodel.factory.ViewModelFactory
import com.mxy.bbs_client.ui.screen.App
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme
import com.mxy.bbs_client.utility.Client

class MainActivity : ComponentActivity() {

    private val viewModelFactory by lazy {
        ViewModelFactory(app = application)
    }

    private val mineScreenViewModel by lazy {
        viewModelFactory.create(MineScreenViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Client.createCacheDatabase(application)
        setContent {
            Bbs_clientTheme {
                App(mineScreenViewModel = mineScreenViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mineScreenViewModel.closeDatabase()
        Client.closeDatabase()
    }
}