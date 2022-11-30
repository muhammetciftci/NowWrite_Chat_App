package com.mtc.nowwrite.ui.fragments.auth_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mtc.nowwrite.R
import com.mtc.nowwrite.databinding.FragmentRegisterBinding
import com.mtc.nowwrite.utils.Resource
import com.mtc.nowwrite.utils.Util
import com.mtc.nowwrite.utils.Util.Companion.isValidEmail
import com.mtc.nowwrite.utils.Util.Companion.toastMessage
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view: View = fragmentBinding.root

        Util.bottomNavInActive(requireActivity())
        registerObserver()
        addUserObserver()
        clickListener()

        return view
    }

    fun clickListener() {
        goLoginClick()
        registerButtonClick()
    }

    private fun registerButtonClick() {
        fragmentBinding.apply {
            registerButton.setOnClickListener() {
                val username = usernameEditTextRegister.text.toString()
                val email = emailEditTextRegister.text.toString()
                val password = passwordEditTextRegister.text.toString()
                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    if(isValidEmail(email) && password.length >= 6){
                        viewModel.register(
                            username = username,
                            email = email,
                            password = password
                        )
                    }else{
                        toastMessage(requireContext(), "invalid email or password length")
                    }
                } else {
                    if (username.isEmpty()) {
                        usernameRegisterTextInputLayout.error = "required to be filled"
                    }
                    if (email.isEmpty() || !isValidEmail(email)) {
                        emailRegisterTextInputLayout.error = "required to be filled"
                    }
                    if (password.isEmpty()) {
                        passwordRegisterTextInputLayout.error = "required to be filled"
                    }
                }
            }
        }
    }

    fun goLoginClick() {
        fragmentBinding.goLoginTextView.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }


    fun registerObserver() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    fragmentBinding.progressBarRegister.show()
                }
                is Resource.Failure -> {
                    fragmentBinding.progressBarRegister.gone()
                    toastMessage(requireContext(), "register falied")
                }
                is Resource.Success -> {
                    fragmentBinding.progressBarRegister.gone()
                }
            }
        }
    }

    fun addUserObserver() {
        viewModel.addUserState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    fragmentBinding.progressBarRegister.show()
                }
                is Resource.Failure -> {
                    fragmentBinding.progressBarRegister.gone()
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        viewModel.auth.currentUser!!.delete()
                    }
                    toastMessage(requireContext(), "database register falied")
                }
                is Resource.Success -> {
                    fragmentBinding.progressBarRegister.gone()
                    toastMessage(requireContext(), "register successfully")
                    viewModel.auth.signOut()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        })
    }

}