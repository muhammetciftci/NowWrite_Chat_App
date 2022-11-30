package com.mtc.nowwrite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.mtc.nowwrite.R
import com.mtc.nowwrite.model.ChatMessage

class ChatMessageAdapter(
    var messageList: ArrayList<ChatMessage>
) :
    RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == MESSAGE_RIGHT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.right_item_row, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.left_item_row, parent, false)
            return ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = messageList[position]
        holder.messageText.text = chat.message
        holder.messageHour.text = chat.hour
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val messageHour: TextView = view.findViewById(R.id.hourText)

    }

    override fun getItemViewType(position: Int): Int {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (messageList[position].senderId == firebaseUser.uid) {
            return MESSAGE_RIGHT
        } else {
            return MESSAGE_LEFT
        }

    }
}