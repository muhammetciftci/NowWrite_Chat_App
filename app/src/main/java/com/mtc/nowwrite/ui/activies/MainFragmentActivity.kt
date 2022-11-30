package com.mtc.nowwrite.ui.activies


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mtc.nowwrite.*
import com.mtc.nowwrite.databinding.ActivityMainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainFragmentBinding
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        bottomNavigationController()

    }


    private fun bottomNavigationController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }


    override fun onStart() {
        super.onStart()
        viewModel.userOnline()
    }

    override fun onStop() {
        super.onStop()
        viewModel.userOffline()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.userOffline()
    }


}







