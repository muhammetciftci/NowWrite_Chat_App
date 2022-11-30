package com.mtc.nowwrite.ui.fragments.auth_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mtc.nowwrite.R
import com.mtc.nowwrite.databinding.FragmentLoginBinding
import com.mtc.nowwrite.utils.Resource
import com.mtc.nowwrite.utils.Util
import com.mtc.nowwrite.utils.Util.Companion.isValidEmail
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentLoginBinding

    private val viewModel by viewModels<AuthViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)
        val view: View = fragmentBinding.root

        Util.bottomNavInActive(requireActivity())
        currentUserControl()
        loginOserver()
        clickListener()

        return view
    }

    fun clickListener() {
        goRegisterClick()
        loginButtonClick()
    }


    fun loginButtonClick() {
        fragmentBinding.apply {
            loginButton.setOnClickListener() {
                val email = emailEditTextLogin.text.toString()
                val password = passwordEditTextLogin.text.toString()
                if (!email.isEmpty() && !password.isEmpty()) {
                    if (isValidEmail(email) && password.length >= 6) {
                        viewModel.loginFirebase(email, password)
                    } else {
                        Util.toastMessage(requireContext(), "invalid email or password length")
                    }

                } else {
                    if (email.isEmpty() || !Util.isValidEmail(email)) {
                        emailLoginTextInputLayout.error = "required to be filled"
                    }
                    if (password.isEmpty()) {
                        passwordLoginTextInputLayout.error = "required to be filled"
                    }
                }
            }
        }
    }

    fun goRegisterClick() {
        fragmentBinding.goRegisterTextView.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        })
    }

    fun loginOserver() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    fragmentBinding.progressBarLogin.show()
                }
                is Resource.Failure -> {
                    fragmentBinding.progressBarLogin.gone()
                    Util.toastMessage(requireContext(), "login falied: check login information.")
                }
                is Resource.Success -> {
                    fragmentBinding.progressBarLogin.gone()
                    findNavController().navigate(R.id.action_loginFragment_to_usersFragment)
                }
            }
        }
    }

    fun currentUserControl() {
        if (viewModel.auth.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_usersFragment)
        }
    }

}