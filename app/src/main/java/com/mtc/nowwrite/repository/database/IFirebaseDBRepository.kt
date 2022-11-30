package com.mtc.nowwrite.repository.database

import android.net.Uri
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.utils.Resource

interface IFirebaseDBRepository {
    fun addUser(user: User, result:(Resource<String>)->Unit)
    fun deleteUser(user: User)
    fun getAllUsers(result: (Resource<ArrayList<User>>) -> Unit)
    fun getUserInfo(userId:String?, result: (Resource<User>) -> Unit)
    fun getAllChats(result: (Resource<ArrayList<ChatMessage>>) -> Unit)
    fun getAllMessage(senderId: String?, receiverId: String?, result: (Resource<ArrayList<ChatMessage>>) -> Unit)
    fun sendMessage(chatMessage: ChatMessage, result: (Resource<String>) -> Unit)
    fun addImage(user:User, imageUri: Uri,result:(Resource<String>)->Unit)
    fun removeImage(user: User)
    fun readedTheMessage(chatId:String)
    fun isOnline(result: (Resource<String>) -> Unit)
    fun isOffline(result: (Resource<String>) -> Unit)
}