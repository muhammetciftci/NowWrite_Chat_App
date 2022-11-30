package com.mtc.nowwrite.model

class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val hour: String = "",
    val read: Boolean = false,
    var chatId:String = "",
)