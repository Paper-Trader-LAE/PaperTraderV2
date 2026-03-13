package com.example.papertraderv2.models

data class ForumPost(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val timestamp: Long = 0,

    var upvotes: Int = 0,
    var downvotes: Int = 0,
    var commentCount: Int = 0
)