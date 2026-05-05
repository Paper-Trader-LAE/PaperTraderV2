package com.example.papertraderv2.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

object ProgressRepository {

    private const val PREFS_NAME = "lesson_progress"
    private const val MIGRATION_DONE_PREFIX = "old_progress_cleared_"

    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    }

    private fun buildKey(lessonKey: String): String {
        return "${getUserId()}_$lessonKey"
    }

    private fun clearOldSharedKeyIfNeeded(context: Context, lessonKey: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val migrationKey = MIGRATION_DONE_PREFIX + getUserId() + "_" + lessonKey

        if (!prefs.getBoolean(migrationKey, false)) {
            prefs.edit()
                .remove(lessonKey) // removes old shared lesson completion
                .putBoolean(migrationKey, true)
                .apply()
        }
    }

    fun isLessonCompleted(context: Context, lessonKey: String): Boolean {
        clearOldSharedKeyIfNeeded(context, lessonKey)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(buildKey(lessonKey), false)
    }

    fun setLessonCompleted(context: Context, lessonKey: String, completed: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(buildKey(lessonKey), completed)
            .apply()
    }

    fun clearAllProgress(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}