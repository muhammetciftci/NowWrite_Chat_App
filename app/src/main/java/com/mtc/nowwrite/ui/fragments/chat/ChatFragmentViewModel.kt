package com.mtc.nowwrite.ui.fragments.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mtc.nowwrite.model.MessageNotification
import com.mtc.nowwrite.model.PushNotification
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.repository.database.IFirebaseDBRepository
import com.mtc.nowwrite.utils.Resource
import com.mtc.nowwrite.utils.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatFragmentViewModel @Inject constructor(val databaseRepository: IFirebaseDBRepository, val auth: FirebaseAuth) :
    ViewModel() {


    private val _message = MutableLiveData<Resource<ArrayList<ChatMessage>>>()
    val message: LiveData<Resource<ArrayList<ChatMessage>>> get() = _message

    private val _receiverUser = MutableLiveData<Resource<User>>()
    val receiverUser: LiveData<Resource<User>> get() = _receiverUser



    fun getUserFirebase(userId: String?) {
        databaseRepository.getUserInfo(userId) {
            _receiverUser.value = it
        }
    }

    fun getAllMessage(senderId: String?, receiverId: String?) {
        _message.value = Resource.Loading
        databaseRepository.getAllMessage(senderId, receiverId) {
            _message.value = it
        }
    }

    fun sendMessage(chatMessage: ChatMessage) {
        databaseRepository.sendMessage(chatMessage){}
    }

    fun sendMessageNotification(title: String, message: String, receiverId: String) {
        val topic = "/topics/$receiverId"
        PushNotification(
            MessageNotification(title, message),
            topic
        ).also {
            Util.sendNotification(it)
        }
    }


    fun readedTheMessage(chatId: String) {
        databaseRepository.readedTheMessage(chatId)
    }


}

