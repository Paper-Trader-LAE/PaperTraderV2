package com.example.papertraderv2.models

data class Module(
    val key: String,          // Unique key for saving progress
    val title: String,
    val description: String,
    val content: String,      // Full lesson text
    val thumbnailRes: Int,    // <-- IMPORTANT: Image for the card
    val progress: Int = 0
)