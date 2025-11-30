package com.example.papertraderv2.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ForumComment(
    @DocumentId val id: String = "",
    val postId: String = "",
    val author: String = "Anonymous",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
) {
    fun getFormattedTime(): String {
        val seconds = Timestamp.now().seconds - timestamp.seconds
        return when {
            seconds < 60 -> "Just now"
            seconds < 3600 -> "${seconds / 60}m ago"
            seconds < 86400 -> "${seconds / 3600}h ago"
            else -> "${seconds / 86400}d ago"
        }
    }
}