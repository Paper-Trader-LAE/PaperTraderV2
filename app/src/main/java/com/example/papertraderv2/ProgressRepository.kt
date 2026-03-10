package com.example.papertraderv2.data

import android.content.Context

object ProgressRepository {

    private const val PREFS = "module_progress"

    fun isLessonCompleted(context: Context, lessonKey: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(lessonKey, false)
    }

    fun setLessonCompleted(context: Context, lessonKey: String, completed: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(lessonKey, completed).apply()
    }
}