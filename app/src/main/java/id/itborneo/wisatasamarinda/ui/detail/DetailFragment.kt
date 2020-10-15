package id.itborneo.wisatasamarinda.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.utils.EXTRA_PLACE
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {
    private val TAG = "DetailFragment"
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)

    }

    private lateinit var wiPlace: WiPlace

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNav(view)
        initData()
        initView()
        initEditListener()

    }

    private fun initEditListener() {
        fabEdit.setOnClickListener {
            actionMoveToAddEditFragment()
        }
    }

    private fun actionMoveToAddEditFragment() {

        val bundle = bundleOf(
            EXTRA_PLACE to wiPlace,
        )
        navController.navigate(R.id.action_detailFragment_to_addUpdateFragment, bundle)

    }

    private fun initNav(view: View) {
        navController = Navigation.findNavController(view)
    }

    private fun initData() {

        val intentPlace = arguments?.getParcelable<WiPlace>(EXTRA_PLACE)
        if (intentPlace != null) {
            Log.d(TAG, intentPlace.toString())
            wiPlace = intentPlace
        }
    }


    private fun initView() {
        tvName.text = wiPlace.name
        tvAddress.text = wiPlace.address
        tvDescription.text = wiPlace.description

        Glide.with(requireContext())
            .load(wiPlace.imagePath)
            .into(ivPlace)


    }


}