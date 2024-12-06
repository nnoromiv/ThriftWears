package com.nnorom.thriftwears.item

import com.google.firebase.Timestamp

data class UserItem(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var phoneNumber: String? = null,
    var id: String? = null,
    var timeStamp: Timestamp? = null
)


