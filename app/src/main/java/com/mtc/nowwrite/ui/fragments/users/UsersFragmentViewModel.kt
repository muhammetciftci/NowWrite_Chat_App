package com.mtc.nowwrite.ui.fragments.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.repository.database.IFirebaseDBRepository
import com.mtc.nowwrite.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersFragmentViewModel @Inject constructor(
    val databaseRepository: IFirebaseDBRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _users = MutableLiveData<Resource<ArrayList<User>>>()
    val user: LiveData<Resource<ArrayList<User>>> get() = _users

    private val _messages = MutableLiveData<Resource<ArrayList<ChatMessage>>>()
    val messages: LiveData<Resource<ArrayList<ChatMessage>>> get() = _messages

    private val _userOfflineState = MutableLiveData<Resource<String>>()
    val userOfflineState: LiveData<Resource<String>> get() = _userOfflineState


    fun getUserListFirebase() {
        _users.value = Resource.Loading
        databaseRepository.getAllUsers {
            _users.value = it
        }
    }

    fun getAllMessage() {
        _messages.value = Resource.Loading
        databaseRepository.getAllChats {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Failure -> {}
                is Resource.Success -> {
                    _messages.value = it
                }
            }

        }
    }


    fun userOffline() {
        databaseRepository.isOffline() {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Failure -> {}
                is Resource.Success -> {
                    _userOfflineState.value = it
                }
            }
        }
    }

    fun userOnline() {
        databaseRepository.isOnline {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Failure -> {}
                is Resource.Success -> {
                    _userOfflineState.value = it
                }
            }
        }
    }

}
