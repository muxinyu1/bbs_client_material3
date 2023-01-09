package com.mxy.bbs_client.program.repository

import com.mxy.bbs_client.program.db.MineScreenStateDataBase
import com.mxy.bbs_client.program.state.MineScreenState

class MineScreenStateRepository(private val _mineScreenStateDataBase: MineScreenStateDataBase) {
    suspend fun add(mineScreenState: MineScreenState) {
        _mineScreenStateDataBase.mineScreenStateDao().add(mineScreenState)
    }

    suspend fun delete(mineScreenState: MineScreenState) {
        _mineScreenStateDataBase.mineScreenStateDao().delete(mineScreenState)
    }

    suspend fun update(mineScreenState: MineScreenState) {
        _mineScreenStateDataBase.mineScreenStateDao().update(mineScreenState)
    }

    suspend fun getMineScreenState(id: Int = 0): MineScreenState? {
        return _mineScreenStateDataBase.mineScreenStateDao().getMineScreenState(id)
    }
}