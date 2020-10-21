package id.itborneo.wisatasamarinda.ui.wisata

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.data.remote.Resource
import id.itborneo.wisatasamarinda.di.ViewModelFactory
import id.itborneo.wisatasamarinda.ui.addUpdate.AddUpdateFragment.Companion.EXTRA_REQ
import id.itborneo.wisatasamarinda.ui.addUpdate.AddUpdateFragment.Companion.REQ_STATUS_ADD
import id.itborneo.wisatasamarinda.utils.*
import kotlinx.android.synthetic.main.fragment_wisata_place.*


@Suppress("DEPRECATION")
class WisataPlaceFragment : Fragment() {

    private lateinit var viewModel: WisataViewModel
    private val TAG = "WisataPlaceFragment"
    private lateinit var adapter: WisataAdapter
    private lateinit var navController: NavController
    private var listPlace = listOf<WiPlace>()

    private val isInternetOn = MutableLiveData(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wisata_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavController(view)
        initBottomBar()
        initFabListener()
        initViewModel()
        getWisataPlaces()
        initRecyclerView()
        internetCheckerAllTime()
    }

    private fun initBottomBar() {
        BottomNavUtils.visible(requireActivity())

    }

    private fun initNavController(view: View) {
        navController = Navigation.findNavController(view)
    }

    private fun initFabListener() {
        fabAdd.setOnClickListener {
            if (isInternetAvailable()) {
                actionToAdd()

            } else {
                Toast.makeText(requireContext(), "Tidak ada Internet", Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun onResume() {

        super.onResume()
        viewModel.onResume()
    }

    private fun actionToAdd() {
        val bundle = bundleOf(
            EXTRA_REQ to REQ_STATUS_ADD,
        )
        navController.navigate(R.id.action_wisataPlaceFragment_to_addUpdateFragment, bundle)
    }


    private fun actionToDetail(wiPlace: WiPlace) {
        val bundle = bundleOf(
            EXTRA_PLACE to wiPlace,
        )
        navController.navigate(R.id.action_wisataPlaceFragment_to_detailFragment, bundle)
    }


    private fun isInternetAvailable(): Boolean {
        val isInsternetAvailable = NetworkUtils(requireContext()).isInternetAvailable()
        RecyclerViewUtils.swipeable = isInsternetAvailable

//        return true
        return isInsternetAvailable
    }

    private fun getWisataPlaces() {
        viewModel.wisataPlaces.observe(viewLifecycleOwner, {
//            Log.d(TAG, "getWisataPlaces : observe  ${it.data?.size}")
            Log.d(TAG, "getWisataPlaces : observe update")
//            Log.d(TAG, "getWisataPlaces : observe  ${it.data}")


            when (it) {
                is Resource.Success -> {
                    //get data and internet is active
                    Log.d(TAG, "getWisataPlaces : Success  ${it.data}")
                    listPlace = DataMappers.mapEntitasToModel(it.data!!)
                    getAllImage2(DataMappers.mapEntitasToModel(it.data))

//                    getAllImage()
                    updateUI(DataMappers.mapEntitasToModel(it.data))
                }
                is Resource.Loading -> {
                    if (it.data != null) {
//                        getAllImage()
                        listPlace = DataMappers.mapEntitasToModel(it.data!!)

                        getAllImage2(DataMappers.mapEntitasToModel(it.data))
                        updateUI(DataMappers.mapEntitasToModel(it.data), false)

                        //getData and no internet
                        Log.d(TAG, "getWisataPlaces : Loading ${it.data.size}")

                    } else {
                        //no getData and no internet
                        Log.d(TAG, "getWisataPlaces : Loading")

                    }

                }
                is Resource.Error -> {
                    Log.d(TAG, "getWisataPlaces : Error")

                }

            }
        })
    }


    private fun updateUI(
        wiPlaces: List<WiPlace>,
        isSynchronized: Boolean = true,
        isDatabaseLoadng: Boolean = false
    ) {

        adapter.setWiPlaces(wiPlaces)
        loading(isDatabaseLoadng)
    }

    private fun loading(showLoadig: Boolean) {
        //loading is here
    }

    private fun initViewModel() {

        val factory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[WisataViewModel::class.java]
    }

    private fun initRecyclerView() {
        rvWiPlace.layoutManager = LinearLayoutManager(context)
//        adapter = MovieAdapter(requireContext()) { getMovie ->
//            //onclickListener
//        }
        adapter = WisataAdapter(requireContext()) {
            //Navigation
            actionToDetail(it)
        }

        rvWiPlace.adapter = adapter
        recyclerViewItemDelete()


    }

    private fun recyclerViewItemDelete() {

        RecyclerViewUtils.attachOnSwipe(requireContext(), rvWiPlace) {

//            ViewsUtils.setDialogComfirm(requireContext(), {
//                Log.d(TAG, "setupOnSwipe + delete ${it.adapterPosition}")

            //delete

            DialogUtls(requireContext()).setDialogDeleteComfirm(requireContext(), {
                Log.d(TAG, "setupOnSwipe + delete ${it.adapterPosition}")
                //delete

                val item = listPlace[it.adapterPosition]
                viewModel.deletePlace(item).observe(viewLifecycleOwner) {
                    Toast.makeText(requireContext(), "Berhasil Menghapus Item", Toast.LENGTH_SHORT)
                        .show()
                }

            }, {
                //adapter.notifyDataSetChanged()

            })


        }

    }

    private fun getAllImage2(listPlace: List<WiPlace>) {
        Log.d(TAG, "getAllImage2")
        listPlace.forEach { getPlace ->
//            Log.d(TAG,"getAllImage2 getPlace")

            viewModel.getOneImage(getPlace.id).observe(viewLifecycleOwner) {
//                Log.d(TAG,"getAllImage2 getPlace id $it")

                getPlace.imagePath = it.toString()
                updateUI(listPlace)
            }
        }
    }


    private fun getAllImage() {


        viewModel.getImageUri(listPlace).observe(requireActivity()) { listUri ->

            if (listUri.isNullOrEmpty()) return@observe


            for (i in listUri.indices) {
                Log.d(TAG, "getAllImage index $i")
                listPlace[i].imagePath = listUri[i].toString()
            }


            Log.d(TAG, "getAllImage $listUri")
            updateUI(listPlace)
        }
    }

    val mainHandler = Handler(Looper.getMainLooper())

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
    }

    private fun internetCheckerAllTime() {


        mainHandler.post(object : Runnable {
            override fun run() {
//                Log.d(TAG, "internetCheckerAllTime is internet on ${isInternetOn.value}")
                isInternetOn.value = isInternetAvailable()
//                RecyclerViewUtils.swipeable = isInternetAvailable()

                mainHandler.postDelayed(this, 1000)

            }
        })

//        val handler = Handler()
//        handler.postDelayed({
//
//        }, 3000)

        isInternetOn.observe(viewLifecycleOwner) {
            showNoInternetNotif(!it)
        }
    }

    private fun showNoInternetNotif(it: Boolean) {
        if (it) {
            tvNoInternetNotif.visibility = View.VISIBLE
        } else {
            tvNoInternetNotif.visibility = View.INVISIBLE
        }
    }


}