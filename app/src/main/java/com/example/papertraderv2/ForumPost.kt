package com.example.papertraderv2.models

data class ForumPost(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val timestamp: Long = System.currentTimeMillis()
)