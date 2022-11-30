package com.mtc.nowwrite.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mtc.nowwrite.utils.Resource

class AuthRepositoryImpl(val auth: FirebaseAuth) : IAuthRepository {

    override fun registerUser(
        username: String,
        email: String,
        password: String,
        result: (Resource<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(Resource.Success("Register successfully"))
                    auth.currentUser!!.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(username).build()
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(Resource.Failure("login failed"))
            }
    }

    override fun loginUser(
        email: String,
        password: String,
        result: (Resource<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(Resource.Success("Login successfully!"))
                }
            }.addOnFailureListener {
                result.invoke(Resource.Failure("login failed"))
            }
    }

    override fun deleteAccount(result: (Resource<String>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null){
            currentUser.delete().addOnCompleteListener {
                if (it.isSuccessful){
                    result.invoke(Resource.Success("successfully"))
                }
            }.addOnFailureListener {
                result.invoke(Resource.Failure(it.message.toString()))
            }
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}