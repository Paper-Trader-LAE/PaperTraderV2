package com.example.papertraderv2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String,
    val action: String,   // "Buy" or "Sell"
    val quantity: Double,
    val price: Double,
    val total: Double,
    val timestamp: Long = System.currentTimeMillis()
)