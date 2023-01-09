package com.mxy.bbs_client.program.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mxy.bbs_client.program.state.MineScreenState
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface MineScreenStateDao {

    @Insert
    suspend fun add(mineScreenState: MineScreenState)

    @Delete
    suspend fun delete(mineScreenState: MineScreenState)

    @Update
    suspend fun update(mineScreenState: MineScreenState)

    @Query("select * from MineScreenState where id = :id")
    suspend fun getMineScreenState(id: Int = 0): MineScreenState?

}