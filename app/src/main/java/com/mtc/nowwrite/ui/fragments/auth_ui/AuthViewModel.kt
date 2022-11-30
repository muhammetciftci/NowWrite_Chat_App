package com.mtc.nowwrite.ui.fragments.auth_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.repository.auth.IAuthRepository
import com.mtc.nowwrite.repository.database.IFirebaseDBRepository
import com.mtc.nowwrite.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authRepository: IAuthRepository,
    val databaseRepository: IFirebaseDBRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableLiveData<Resource<String>>()
    val login: LiveData<Resource<String>> get() = _login

    private val _register = MutableLiveData<Resource<String>>()
    val register: LiveData<Resource<String>> get() = _register

    private val _addUserState = MutableLiveData<Resource<String>>()
    val addUserState: LiveData<Resource<String>> get() = _addUserState

    fun loginFirebase(email: String, password: String) {
        _login.value = Resource.Loading
        authRepository.loginUser(email, password) {
            _login.value = it
        }
    }

    fun register(username: String, email: String, password: String) {
        _register.value = Resource.Loading
        authRepository.registerUser(username, email, password) { databaseRegisterState ->
            _register.value = databaseRegisterState
            val user = User(auth.currentUser!!.uid, username, email)
            addUser(user)
        }
    }
    
    fun addUser(user: User){
        databaseRepository.addUser(user) { databaseAddUserState ->
            _addUserState.value = databaseAddUserState
        }
    }
}

