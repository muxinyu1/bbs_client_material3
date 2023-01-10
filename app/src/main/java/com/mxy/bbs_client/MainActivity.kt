package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.program.viewmodel.factory.ViewModelFactory
import com.mxy.bbs_client.ui.screen.App
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme


class MainActivity : ComponentActivity(), ImageLoaderFactory {

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
        val windowInsetsController = WindowInsetsControllerCompat(
            window, window.decorView
        )
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // Client.createCacheDatabase(application)
        setContent {
            Bbs_clientTheme {
                App(mineScreenViewModel = mineScreenViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mineScreenViewModel.closeDatabase()
        // Client.closeDatabase()
    }

    override fun onResume() {
        super.onResume()
        val windowInsetsController = WindowInsetsControllerCompat(
            window, window.decorView
        )
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(this.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}