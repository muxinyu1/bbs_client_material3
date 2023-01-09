package com.mxy.bbs_client.program.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mxy.bbs_client.program.dao.MineScreenStateDao
import com.mxy.bbs_client.program.state.MineScreenState

@Database(entities = [MineScreenState::class], version = 2)
abstract class MineScreenStateDataBase: RoomDatabase() {
    abstract fun mineScreenStateDao(): MineScreenStateDao
}