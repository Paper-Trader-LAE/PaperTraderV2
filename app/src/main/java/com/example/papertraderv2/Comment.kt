package com.example.papertraderv2.models

data class Comment(
    val id: String = "",
    val postId: String = "",
    val author: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)