package com.mtc.nowwrite.model

data class User(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    var downloadUrl: String = "",
    val onlineStatus:Boolean = false
)