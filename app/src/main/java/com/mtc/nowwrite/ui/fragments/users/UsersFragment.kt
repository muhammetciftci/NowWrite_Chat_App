package com.mtc.nowwrite.ui.fragments.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mtc.nowwrite.R
import com.mtc.nowwrite.adapter.IAdapterClickListener
import com.mtc.nowwrite.adapter.UserAdapter
import com.mtc.nowwrite.databinding.FragmentUsersBinding
import com.mtc.nowwrite.model.ChatMessage
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.service.FirebaseNotificationService
import com.mtc.nowwrite.utils.Util
import com.mtc.nowwrite.utils.Resource
import com.mtc.nowwrite.utils.Util.Companion.toastMessage
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UsersFragment : Fragment(), IAdapterClickListener {


    private lateinit var fragmentBinding: FragmentUsersBinding
    private lateinit var userAdapter: UserAdapter
    private var userList: ArrayList<User> = arrayListOf()
    private var messageList: ArrayList<ChatMessage> = arrayListOf()

    private val viewModel by viewModels<UsersFragmentViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentBinding = FragmentUsersBinding.inflate(inflater, container, false)
        val view = fragmentBinding.root

        Util.bottomNavActive(requireActivity())
        subscripeTopic()
        updateNewToken()
        toolbarClickListener(view)
        observeUserList()
        observeMessage()
        refreshRecylerView()


        return view
    }


    fun refreshRecylerView() {
        userAdapter = UserAdapter(userList, messageList, this)
        fragmentBinding.recylerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        fragmentBinding.recylerViewUsers.adapter = userAdapter
    }

    fun subscripeTopic() {
        var userid = viewModel.auth.currentUser!!.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")
    }


    fun toolbarClickListener(view: View) {
        fragmentBinding.toolbarUsers.inflateMenu(R.menu.home_menu)
        fragmentBinding.toolbarUsers.setOnMenuItemClickListener {
            if (it.itemId == R.id.sign_out_item) {
                showExitDialog()
            }
            true
        }
    }

    fun showExitDialog() {
        val builder = AlertDialog.Builder(this@UsersFragment.requireContext())
        val dialog = builder.create()
        dialog.show()
        dialog.setContentView(R.layout.exit_dialog)

        val yesButton = dialog.findViewById(R.id.yesButton_ExitDialog) as Button
        val noButton = dialog.findViewById(R.id.noButton_ExitDialog) as Button

        yesButton.setOnClickListener() {
            viewModel.userOffline()
            observeUserOffline()
            dialog.dismiss()
        }
        noButton.setOnClickListener() {
            dialog.dismiss()
        }
    }

    fun observeUserList() {
        viewModel.getUserListFirebase()
        viewModel.user.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    fragmentBinding.animationViewUsers.show()
                }
                is Resource.Failure -> {
                    fragmentBinding.animationViewUsers.gone()
                    fragmentBinding.animationViewUsers.pauseAnimation()
                    toastMessage(requireContext(), it.error.toString())
                }
                is Resource.Success -> {
                    fragmentBinding.animationViewUsers.gone()
                    fragmentBinding.animationViewUsers.pauseAnimation()
                    userList.clear()
                    userList = it.data
                    refreshRecylerView()
                }
            }
        })
    }

    fun observeMessage() {
        viewModel.getAllMessage()
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Failure -> {}
                is Resource.Success -> {
                    messageList.clear()
                    messageList = it.data
                    refreshRecylerView()
                }
            }
        })
    }

    fun observeUserOffline() {
        viewModel.userOfflineState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Failure -> {}
                is Resource.Success -> {
                    viewModel.auth.signOut()
                    findNavController().navigate(R.id.action_usersFragment_to_loginFragment)
                    toastMessage(requireContext(), "Sign out is successful")
                }
            }
        })
    }


    override fun onUserClick(user: User) {
        val bundle = Bundle()
        bundle.putString("userid", user.userId)
        bundle.putString("username", user.userName)
        bundle.putString("useremail", user.userEmail)
        bundle.putString("downloadurl", user.downloadUrl)
        findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)

    }

    fun updateNewToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                FirebaseNotificationService.token = token
            }
        })
    }


    override fun onStart() {
        super.onStart()
        viewModel.userOnline()
    }


}