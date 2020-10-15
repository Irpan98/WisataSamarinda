package id.itborneo.wisatasamarinda.ui.profil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_profil.*


class ProfilFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private val TAG = "ProfilFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
        initLogOutListener()
    }

    private fun initView() {
        Log.d(TAG, "initView ${viewModel.user}")

        tvUser.text = viewModel.user.name
        tvEmail.text = viewModel.user.email

        Glide.with(requireContext())
            .load(viewModel.user.photoUrl)
            .into(ivUser)
    }

    private fun initViewModel() {
        viewModel = activity?.run {
            ViewModelProvider(
                requireActivity(),
                ViewModelProvider.NewInstanceFactory()
            )[MainViewModel::class.java]
        } ?: throw Exception("Salah Activity")
    }

    private val mAuth = FirebaseAuth.getInstance()


    private fun initLogOutListener() {
        btnLogOut.setOnClickListener {

            mAuth.signOut()
        }

        mAuth.addAuthStateListener {
            if (mAuth.currentUser == null) {
                activity?.onBackPressed()
            }
        }
    }


}