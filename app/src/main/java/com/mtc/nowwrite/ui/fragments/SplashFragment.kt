package com.mtc.nowwrite.ui.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mtc.nowwrite.R
import com.mtc.nowwrite.databinding.FragmentSplashBinding
import com.mtc.nowwrite.utils.Util
import com.mtc.nowwrite.utils.Util.Companion.connectionControl
import com.mtc.nowwrite.utils.Util.Companion.toastMessage
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show


class SplashFragment : Fragment() {


    private lateinit var fragmentBinding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentBinding = FragmentSplashBinding.inflate(inflater, container, false)
        val view:View= fragmentBinding.root

        Util.bottomNavInActive(requireActivity())
        connectionControlFragment()
        clickListener()

        return view
    }

    fun clickListener(){
        fragmentBinding.reconnectButton.setOnClickListener(){
            connectionControlFragment()
        }
    }

    fun splashScreen() {
        Handler().postDelayed({
            fragmentBinding.animationViewSplash.pauseAnimation()
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null){
                findNavController().navigate(R.id.action_splashFragment_to_usersFragment)
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 2000)
    }

    fun connectionControlFragment() {
        if (connectionControl(requireContext())) {
            splashScreen()
            fragmentBinding.apply {
                reconnectButton.gone()
                noIntenetAnimationViewSplash.gone()
                animationViewSplash.show()
            }
        } else {
            fragmentBinding.apply {
                toastMessage(requireContext(), "failed connection... :(")
                reconnectButton.show()
                noIntenetAnimationViewSplash.show()
                animationViewSplash.gone()
            }
        }
    }

}