package com.example.thriftwears.item

import android.net.Uri

data class CardViewItem(
    val id: String,
    val title: String,
    val image: Uri,
    val description: String,
    val price: Double
)