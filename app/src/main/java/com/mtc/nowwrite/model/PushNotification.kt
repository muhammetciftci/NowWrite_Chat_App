package com.mtc.nowwrite.model

data class PushNotification(
    val data: MessageNotification,
    val to: String
)