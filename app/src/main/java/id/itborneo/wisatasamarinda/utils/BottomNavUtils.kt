package id.itborneo.wisatasamarinda.utils

import android.app.Activity
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.itborneo.wisatasamarinda.R

object BottomNavUtils {

    fun visible(activity: Activity?) {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
    }
    fun invisible(activity: Activity?) {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE
    }
}