package com.mtc.nowwrite.ui.fragments.chat

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.mtc.nowwrite.*
import com.mtc.nowwrite.adapter.ChatMessageAdapter
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.utils.Util
import com.mtc.nowwrite.databinding.FragmentChatBinding
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.utils.Resource
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView


@AndroidEntryPoint
class ChatFragment : Fragment() {


    private lateinit var fragmentBinding: FragmentChatBinding

    private lateinit var chatMessageAdapter: ChatMessageAdapter

    private var chatList: ArrayList<ChatMessage> = arrayListOf()

    private lateinit var currentUser: FirebaseUser
    private lateinit var receiverUser: User

    private val viewModel by viewModels<ChatFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentChatBinding.inflate(inflater, container, false)
        val view: View = fragmentBinding.root

        currentUser = viewModel.auth.currentUser!!

        Util.bottomNavInActive(requireActivity())
        recylerViewInitialize()
        getReceiverUserBundle()
        observeReceiverUserInfo()
        messageObserve()
        clickListener()

        return view
    }

    fun clickListener() {
        sendButtonClick()
        backButtonClick()
        profileImageClick()
    }

    fun sendButtonClick() {
        fragmentBinding.sendMessageButtonChat.setOnClickListener{
            val message = fragmentBinding.messageEdittextChat.text.toString()
            val hour = Util.getHourNow()
            if (!message.isEmpty()) {
                val receiverId = receiverUser.userId
                val senderId = currentUser.uid
                val chatMessage = ChatMessage(senderId, receiverId!!, message, hour!!)
                val senderName = currentUser.displayName!!
                viewModel.sendMessage(chatMessage)
                viewModel.sendMessageNotification(senderName, message, receiverId)
                fragmentBinding.messageEdittextChat.text.clear()

            } else {
                Util.toastMessage(requireContext(), "blank message!")
            }
        }
    }

    fun backButtonClick() {
        fragmentBinding.backButtonChat.setOnClickListener{
            findNavController().navigate(R.id.action_chatFragment_to_usersFragment)
        }
    }

    fun profileImageClick() {
        fragmentBinding.profileImageViewChat.setOnClickListener{
            showProfileDialog()
        }
    }

    fun showProfileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialog = builder.create()
        dialog.show()
        dialog.setContentView(R.layout.profile_dialog)

        val closeButton = dialog.findViewById(R.id.closeButtonDialog) as Button
        val profileImageView = dialog.findViewById(R.id.profileImage_ProfileDialog) as CircleImageView
        val usernameTextView = dialog.findViewById(R.id.username_ProfileDialog) as TextView

        //for dialog
        if (receiverUser.downloadUrl == "") {
            Glide.with(requireContext()).load(R.drawable.ic_outline_person_outline_24).into(profileImageView)
        } else {
            Glide.with(requireContext()).load(receiverUser.downloadUrl).into(profileImageView)
        }

        usernameTextView.text = receiverUser.userName

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun getReceiverUserBundle() {
        val bundle = arguments
        if (bundle != null) {
            val receiverId = bundle.getString("userid")
            val username = bundle.getString("username")
            val useremail = bundle.getString("useremail")
            val downloadurl = bundle.getString("downloadurl")
            receiverUser = User(receiverId!!, username!!, useremail!!, downloadurl!!)
        }
    }

    fun messageObserve() {
        viewModel.getAllMessage(currentUser.uid, receiverUser.userId)
        viewModel.message.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    Util.toastMessage(requireContext(), state.error.toString())
                }
                is Resource.Success -> {
                    chatList = state.data
                    refreshrecylerView()
                }
            }
        })
    }

    fun observeReceiverUserInfo() {
        viewModel.getUserFirebase(receiverUser.userId)
        viewModel.receiverUser.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    Util.toastMessage(requireContext(), state.error.toString())
                }
                is Resource.Success -> {
                    fragmentBinding.receiver = state.data
                    setReceiverUserProfileImage(state.data.downloadUrl)
                    setReceiverOnlineState(state.data.onlineStatus)
                }
            }
        })
    }

    fun setReceiverUserProfileImage(downloadUrl: String) {
        if (downloadUrl == "") {
            Glide.with(requireContext()).load(R.drawable.ic_outline_person_outline_24).into(fragmentBinding.profileImageViewChat)
        } else {
            Glide.with(requireContext()).load(downloadUrl).into(fragmentBinding.profileImageViewChat)
        }
    }

    fun setReceiverOnlineState(state: Boolean) {
        if (state) {
            fragmentBinding.onlineStatusText.show()
        } else {
            fragmentBinding.onlineStatusText.gone()
        }
    }

    fun refreshrecylerView() {
        chatMessageAdapter.messageList = chatList
        fragmentBinding.recylerViewChatscreen.scrollToPosition(chatList.size - 1);
        if (!chatList.isEmpty()) {
            chatList.forEach {
                if (it.receiverId == currentUser!!.uid) {
                    if (!it.read) {
                        viewModel.readedTheMessage(it.chatId)
                    }
                }
            }
        }
    }

    fun recylerViewInitialize() {
        chatMessageAdapter = ChatMessageAdapter(chatList)
        fragmentBinding.recylerViewChatscreen.layoutManager = LinearLayoutManager(requireContext())
        fragmentBinding.recylerViewChatscreen.adapter = chatMessageAdapter
    }

}
