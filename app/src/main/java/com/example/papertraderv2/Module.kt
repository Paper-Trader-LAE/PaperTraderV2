package com.example.papertraderv2.models

data class Module(
    val key: String,
    val title: String,
    val description: String,
    val thumbnailRes: Int,
    val lessons: List<Lesson>,
    val progress: Int = 0
)