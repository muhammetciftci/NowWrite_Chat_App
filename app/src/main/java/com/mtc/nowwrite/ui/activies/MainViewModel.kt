package com.mtc.nowwrite.ui.activies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mtc.nowwrite.repository.database.IFirebaseDBRepository
import com.mtc.nowwrite.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val databaseRepository: IFirebaseDBRepository
) : ViewModel() {

    private val _userOnlineState = MutableLiveData<Resource<String>>()
    val userOnlineState: LiveData<Resource<String>> get() = _userOnlineState

    private val _userOfflineState = MutableLiveData<Resource<String>>()
    val userOfflineState: LiveData<Resource<String>> get() = _userOfflineState

    fun userOnline() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            databaseRepository.isOnline() {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Failure -> {}
                    is Resource.Success -> {
                        _userOnlineState.value = it
                    }
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
                    _userOnlineState.value = it
                }
            }

        }
    }

}