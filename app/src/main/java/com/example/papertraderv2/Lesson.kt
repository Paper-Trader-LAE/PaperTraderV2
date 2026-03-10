package com.example.papertraderv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    val key: String,
    val title: String,
    val summary: String,
    val content: String
) : Parcelable