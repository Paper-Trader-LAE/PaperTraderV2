package com.example.papertraderv2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.papertraderv2.models.Trade

@Dao
interface TradeDao {

    @Insert
    suspend fun insertTrade(trade: Trade)

    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    suspend fun getAllTrades(): List<Trade>
}