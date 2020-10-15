package id.itborneo.wisatasamarinda.ui.splash

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.utils.BottomNavUtils
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : Fragment() {
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavController(view)
        initTimerToNavigation()
        hideBottomNav()
    }

    private fun initTimerToNavigation() {
        val handler = Handler()
        handler.postDelayed({
            spinKitLoading.visibility = View.GONE
            navController.navigate(R.id.action_splashFragment_to_loginFragment)
        }, 2000)
    }

    private fun hideBottomNav() {
        BottomNavUtils.invisible(activity)
    }


    private fun initNavController(view: View) {
        navController = Navigation.findNavController(view)

    }


}