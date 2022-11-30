package com.mtc.nowwrite.ui.fragments.profile


import android.net.Uri
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
class ProfileFragmentViewModel @Inject constructor(
    val repositoryDatabase: IFirebaseDBRepository,
    val auth: FirebaseAuth,
    val authRepository: IAuthRepository
) : ViewModel() {


    private val _userInfoState = MutableLiveData<Resource<User>>()
    val userInfoState: LiveData<Resource<User>> get() = _userInfoState


    private val _addImageState = MutableLiveData<Resource<String>>()
    val addImageState: LiveData<Resource<String>> get() = _addImageState


    private val _updateUserState = MutableLiveData<Resource<String>>()
    val updateUserState: LiveData<Resource<String>> get() = _updateUserState

    private val _deleteAccountState = MutableLiveData<Resource<String>>()
    val deleteAccountState: LiveData<Resource<String>> get() = _deleteAccountState


    //for image
    fun updateUserFirebase(user: User, imageUri: Uri) {
        if (!user.userName.isEmpty() && !user.userEmail.isEmpty()) {
            val updatedUser = User(user.userId, user.userName, user.userEmail, user.downloadUrl)
            repositoryDatabase.addImage(updatedUser, imageUri) {
                _addImageState.value = it
            }
        }
    }

    // for only info
    fun updateUserFirebase(user: User) {
        if (!user.userName.isEmpty() && !user.userEmail.isEmpty()) {
            val updatedUser = User(user.userId, user.userName, user.userEmail, user.downloadUrl)
            repositoryDatabase.addUser(user) {
                _updateUserState.value = it
            }
        }
    }

    fun removeUserImage(user: User) {
        repositoryDatabase.removeImage(user)
    }


    fun getUserFirebase(userId: String?) {
        repositoryDatabase.getUserInfo(userId) {
            _userInfoState.value = it
        }
    }

    fun deleteAccount(user: User) {
        authRepository.deleteAccount {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Failure -> {

                }
                is Resource.Success -> {
                    repositoryDatabase.deleteUser(user)
                }
            }
        }
    }


}