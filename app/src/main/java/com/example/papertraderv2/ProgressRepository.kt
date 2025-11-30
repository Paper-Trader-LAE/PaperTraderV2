package com.example.papertraderv2.data

import android.content.Context

object ProgressRepository {

    private const val PREFS = "module_progress"

    fun getProgress(context: Context, key: String): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }

    fun setProgress(context: Context, key: String, value: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt(key, value).apply()
    }
}