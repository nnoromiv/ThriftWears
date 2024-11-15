package com.example.thriftwears.card

import android.net.Uri

data class Item(
    val title: String,
    val image: Uri,
    val description: String,
    val price: Double
)