package com.mtc.nowwrite.repository.database

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.utils.Resource
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirebaseDBRepositoryImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseStorage: FirebaseStorage,
    val storageReference: StorageReference,
    val auth: FirebaseAuth
) : IFirebaseDBRepository {

    override fun addUser(user: User, result: (Resource<String>) -> Unit) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["userId"] = user.userId
        hashMap["userName"] = user.userName
        hashMap["userEmail"] = user.userEmail
        hashMap["downloadUrl"] = user.downloadUrl
        hashMap["onlineStatus"] = user.onlineStatus
        firebaseDatabase.getReference("Users").child(user.userId).setValue(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(Resource.Success("register successfully"))
                } else {
                    result.invoke(Resource.Failure("register Failure"))
                }
            }
    }

    override fun deleteUser(user: User,) {
        firebaseDatabase.getReference("Users").child(user.userId).removeValue()
    }

    override fun getAllUsers(result: (Resource<ArrayList<User>>) -> Unit) {
        firebaseDatabase.getReference("Users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = arrayListOf<User>()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    if (user!!.userId != auth.currentUser?.uid) {
                        usersList.add(user)
                    }
                }
                result.invoke(
                    Resource.Success(usersList)
                )
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(Resource.Failure(error.message))
            }

        })
    }

    override fun getUserInfo(userId: String?, result: (Resource<User>) -> Unit) {
        firebaseDatabase.getReference("Users").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null){
                        result.invoke(Resource.Success(user))
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    result.invoke(Resource.Failure(error.message))
                }

            })
    }

    override fun getAllMessage(
        senderId: String?,
        receiverId: String?,
        result: (Resource<ArrayList<ChatMessage>>) -> Unit) {
        firebaseDatabase.getReference("Chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = arrayListOf<ChatMessage>()
                for (ds: DataSnapshot in snapshot.children) {
                    val chat = ds.getValue(ChatMessage::class.java)
                    chat!!.chatId = ds.key!!
                    if (chat.receiverId == receiverId && chat.senderId == senderId ||
                        chat.receiverId == senderId && chat.senderId == receiverId
                    ) {
                        messageList.add(chat)
                    }
                }
                result.invoke(
                    Resource.Success(messageList)
                )
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(Resource.Failure(error.message))
            }
        })
    }


    override fun getAllChats(result: (Resource<ArrayList<ChatMessage>>) -> Unit) {
        firebaseDatabase.getReference("Chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = arrayListOf<ChatMessage>()
                for (ds: DataSnapshot in snapshot.children) {
                    val chat = ds.getValue(ChatMessage::class.java)
                    messageList.add(chat!!)
                }
                result.invoke(
                    Resource.Success(messageList)
                )
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(Resource.Failure(error.message))
            }
        })
    }


    override fun sendMessage(chatMessage: ChatMessage, result: (Resource<String>) -> Unit) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["senderId"] = chatMessage.senderId
        hashMap["receiverId"] = chatMessage.receiverId
        hashMap["message"] = chatMessage.message
        hashMap["hour"] = chatMessage.hour
        hashMap["read"] = false
        firebaseDatabase.getReference().child("Chats").push().setValue(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(Resource.Success("Message sending successfully"))
                } else {
                    result.invoke(Resource.Failure("Message sending not successfully"))
                }
            }
    }

    override fun addImage(user: User, imageUri: Uri, result: (Resource<String>) -> Unit) {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        storageReference.child("images").child(imageName).putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                firebaseStorage.reference.child("images")
                    .child(imageName).downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            user.downloadUrl = it.result.toString()
                            addUser(user) { dbState ->
                                when (dbState) {
                                    Resource.Loading -> {
                                        result.invoke(Resource.Loading)
                                    }
                                    is Resource.Failure -> {
                                        result.invoke(Resource.Failure("not successfully"))
                                    }

                                    is Resource.Success -> {
                                        result.invoke(Resource.Success("successfully"))
                                    }
                                }

                            }
                        }
                    }.addOnFailureListener {
                        result.invoke(Resource.Failure("Image upload not successfully"))
                    }
            }.addOnFailureListener {
                result.invoke(Resource.Failure("Image upload not successfully"))
            }
    }

    override fun removeImage(user: User) {
        firebaseDatabase.getReference("Users").child(user.userId).child("downloadUrl").setValue("")
    }

    override fun readedTheMessage(chatId: String) {
        firebaseDatabase.getReference("Chats").child(chatId).child("read").setValue(true)
    }


    override fun isOnline(result: (Resource<String>) -> Unit) {
        if (auth.currentUser != null) {
            val currentUserId = auth.currentUser!!.uid
            firebaseDatabase.getReference("Users").child(currentUserId).child("onlineStatus")
                .setValue(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(Resource.Success("successfully"))
                }
            }.addOnFailureListener {
                result.invoke(Resource.Failure("not successfully"))
            }
        }

    }

    override fun isOffline(result: (Resource<String>) -> Unit) {
        if (auth.currentUser != null) {
            val currentUserId = auth.currentUser!!.uid
            firebaseDatabase.getReference("Users").child(currentUserId).child("onlineStatus")
                .setValue(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(Resource.Success("successfully"))
                }
            }.addOnFailureListener {
                result.invoke(Resource.Failure("not successfully"))
            }
        }
    }


}