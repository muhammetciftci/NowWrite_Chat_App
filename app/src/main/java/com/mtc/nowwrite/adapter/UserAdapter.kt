package com.mtc.nowwrite.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.mtc.nowwrite.R
import com.mtc.nowwrite.databinding.ItemUserRowBinding
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(
    val usersList: ArrayList<User>,
    val messageList: ArrayList<ChatMessage>,
    var listener: IAdapterClickListener
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = usersList[position]
        holder.binding.usernameUserrow.text = user.userName

        checkProfileImage(user.downloadUrl, holder)

        itemClickListener(user, holder.itemView)

        messageList.forEach { chat ->
            checkLastMessage(chat, holder, position)
        }

    }


    override fun getItemCount(): Int {
        return usersList.size
    }

    class ViewHolder(val binding: ItemUserRowBinding) : RecyclerView.ViewHolder(binding.root) {}

    fun itemClickListener(user: User, view: View) {
        view.setOnClickListener(View.OnClickListener {
            listener.onUserClick(user)
        })
    }

    fun checkLastMessage(chatMessage: ChatMessage, holder: ViewHolder, position: Int) {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        if (chatMessage.receiverId == currentUserId && chatMessage.senderId == usersList[position].userId) {
            holder.binding.lastTextUserrow.text = chatMessage.message
            holder.binding.hourUserrow.text = chatMessage.hour
            if (chatMessage.read) {
                holder.binding.unreadCircle.gone()
            } else {
                holder.binding.unreadCircle.show()
                YoYo.with(Techniques.Bounce).duration(1000).repeat(0)
                    .playOn(holder.binding.unreadCircle)
            }
        } else if (chatMessage.senderId == currentUserId && chatMessage.receiverId == usersList[position].userId) {
            holder.binding.lastTextUserrow.text = chatMessage.message
            holder.binding.hourUserrow.text = chatMessage.hour
        }
    }

    fun checkProfileImage(downloadUrl: String, holder: ViewHolder) {
        if (downloadUrl == "") {
            Glide.with(holder.itemView.context).load(R.drawable.ic_outline_person_outline_24)
                .into(holder.binding.profileImageUserrow)
        } else {
            Glide.with(holder.itemView.context).load(downloadUrl)
                .into(holder.binding.profileImageUserrow)
        }
    }


}