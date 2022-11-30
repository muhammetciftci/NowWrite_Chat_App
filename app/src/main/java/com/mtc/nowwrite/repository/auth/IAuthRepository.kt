package com.mtc.nowwrite.repository.auth

import com.google.firebase.auth.FirebaseUser
import com.mtc.nowwrite.utils.Resource

interface IAuthRepository {

    fun registerUser(username:String, email: String, password: String,result: (Resource<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (Resource<String>) -> Unit)
    fun deleteAccount(result: (Resource<String>) -> Unit)
    fun signOut()

}