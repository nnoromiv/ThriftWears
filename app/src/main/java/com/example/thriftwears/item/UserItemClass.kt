package com.example.thriftwears.item

import com.google.firebase.Timestamp

data class UserItemClass(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var id: String? = null,
    var timeStamp: Timestamp? = null
)


