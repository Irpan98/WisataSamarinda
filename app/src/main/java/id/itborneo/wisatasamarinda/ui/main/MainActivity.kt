package id.itborneo.wisatasamarinda.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import id.itborneo.wisatasamarinda.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRemoveActionBar()
        initBottoNav()

    }

    private fun initRemoveActionBar() {
        supportActionBar?.hide()
    }


    private fun initBottoNav() {
        val navController = Navigation.findNavController(this, R.id.homeNavHostFragment)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }


}