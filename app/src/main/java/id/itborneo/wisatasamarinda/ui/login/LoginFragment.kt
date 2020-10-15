package id.itborneo.wisatasamarinda.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.User
import id.itborneo.wisatasamarinda.ui.main.MainViewModel
import id.itborneo.wisatasamarinda.utils.BottomNavUtils
import id.itborneo.wisatasamarinda.utils.DialogUtls
import id.itborneo.wisatasamarinda.utils.NetworkUtils
import id.itborneo.wisatasamarinda.utils.SpinKitUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.partial_loading.*


class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"
    private lateinit var navController: NavController
    private lateinit var googleLogin: GoogleLogin
    private lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav()
        initViewModel()
        initNavController(view)
        initAuthGoogleListener()
        checkLastLogin()

    }

    private fun hideBottomNav() {
        BottomNavUtils.invisible(activity)
    }

    private fun checkLastLogin() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser = mAuth.currentUser ?: return
        val user = User(
            currentUser.displayName ?: "",
            currentUser.email ?: "",
            currentUser.photoUrl.toString()

        )
        actionToPlace(user)
    }

    private fun initAuthGoogleListener() {
        googleLogin = GoogleLogin(this)
        googleLogin.initAuthGmail()
        btnLogin.setOnClickListener {
            if (!isInternetAvailable()) return@setOnClickListener
            loading()

            googleLogin.signIn()
        }
    }


    private fun actionToPlace(user: User) {

        viewModel.user = user
//        val bundle = bundleOf(
//            EXTRA_USER to user
//        )
        navController.navigate(R.id.action_loginFragment_to_wisataPlaceFragment)
    }

    private fun initNavController(view: View) {
        navController = Navigation.findNavController(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult:" + "onActivityResult")
        loading(false)

        googleLogin.onActivityResult(requestCode, resultCode, data) {

            val user = User(
                it.displayName ?: "",
                it.email ?: "",
                it.photoUrl.toString(),
            )
            actionToPlace(user)
        }
    }

    private fun initViewModel() {
        viewModel = activity?.run {
            ViewModelProvider(
                requireActivity(),
                ViewModelProvider.NewInstanceFactory()
            )[MainViewModel::class.java]
        } ?: throw Exception("Salah Activity")
    }

    private fun isInternetAvailable(): Boolean {
        return if (NetworkUtils(requireContext()).isInternetAvailable()) {
            DialogUtls(requireContext()).internetAvailable()
            true
        } else {
            DialogUtls(requireContext()).setDialogNoInternet {
                isInternetAvailable()
            }
            false
        }
    }

    private fun loading(showLoading: Boolean = true) {
        if (showLoading) {
            SpinKitUtils.show(spinKitLoading)
        } else {
            SpinKitUtils.hide(spinKitLoading)
        }
    }

}