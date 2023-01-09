package com.mxy.bbs_client.program.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel

class ViewModelFactory(val app: Application) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MineScreenViewModel::class.java)) {
            return MineScreenViewModel(app) as T
        }
        throw IllegalArgumentException("未知的ViewModel类型")
    }
}