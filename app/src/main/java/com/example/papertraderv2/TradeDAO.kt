package com.example.papertraderv2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.papertraderv2.models.Trade

@Dao
interface TradeDao {

    @Insert
    suspend fun insertTrade(trade: Trade)

    @Query("SELECT * FROM trades WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getTradesForUser(userId: String): List<Trade>

    @Query("DELETE FROM trades WHERE userId = :userId")
    suspend fun deleteTradesForUser(userId: String)
}