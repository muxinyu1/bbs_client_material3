package com.mxy.bbs_client

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.program.viewmodel.factory.ViewModelFactory
import com.mxy.bbs_client.ui.component.SavableAsyncImage
import com.mxy.bbs_client.ui.component.vibrator
import com.mxy.bbs_client.ui.screen.App
import com.mxy.bbs_client.ui.theme.Bbs_clientTheme


@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : ComponentActivity(), ImageLoaderFactory {

    private val viewModelFactory by lazy {
        ViewModelFactory(app = application)
    }

    private val mineScreenViewModel by lazy {
        viewModelFactory.create(MineScreenViewModel::class.java)
    }

    private val vibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Client.createCacheDatabase(application)
        setContent {
            Bbs_clientTheme {
                App(mineScreenViewModel = mineScreenViewModel)
//                SavableAsyncImage(
//                    model = "http://nahida8848.xyz:8086/home/nginx_root/repo/server/avatars/default.png",
//                    contentDescription = null,
//                    modifier = Modifier
//                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mineScreenViewModel.closeDatabase()
        // Client.closeDatabase()
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