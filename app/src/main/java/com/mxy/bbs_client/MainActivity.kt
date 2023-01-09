package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.mxy.bbs_client.program.viewmodel.AppViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.program.viewmodel.factory.ViewModelFactory
import com.mxy.bbs_client.ui.screen.App
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility

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
        setContent {
            Bbs_clientTheme {
                App(mineScreenViewModel = mineScreenViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mineScreenViewModel.closeDataBase()
    }
}