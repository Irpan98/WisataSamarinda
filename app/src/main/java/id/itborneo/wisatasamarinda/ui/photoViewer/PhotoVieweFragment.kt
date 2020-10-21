package id.itborneo.wisatasamarinda.ui.photoViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.utils.BottomNavUtils
import id.itborneo.wisatasamarinda.utils.EXTRA_PLACE
import kotlinx.android.synthetic.main.fragment_add_update.*


class PhotoVieweFragment : Fragment() {

    private lateinit var wiPlace: WiPlace

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_viewe, container, false)
    }

    private fun initData() {

        val intentPlace = arguments?.getParcelable<WiPlace>(EXTRA_PLACE)
        if (intentPlace != null) {
            wiPlace = intentPlace
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initBottomBar()
        updateUI()
    }

    private fun updateUI() {
        Glide.with(requireContext())
            .load(wiPlace.imagePath)
            .into(ivPlace)
    }

    private fun initBottomBar() {
        BottomNavUtils.invisible(activity)
    }


}