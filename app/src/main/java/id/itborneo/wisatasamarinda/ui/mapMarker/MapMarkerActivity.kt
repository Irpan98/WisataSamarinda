package id.itborneo.wisatasamarinda.ui.mapMarker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.ui.addUpdate.AddUpdateFragment
import id.itborneo.wisatasamarinda.utils.EXTRA_PLACE
import id.itborneo.wisatasamarinda.utils.MapUtils
import kotlinx.android.synthetic.main.activity_map_marker.*


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapMarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapUtils: MapUtils
    private val TAG = "MapMarkerActivity"
    private lateinit var activityCaller: String
    private var place = WiPlace()
    private var getLocation = LatLng(0.0, 0.0)

    companion object {
        const val REQ_MAP = 20

        const val EXTRA_FROM_ADD_UPDATE = "outline fragment"
        const val EXTRA_FROM_REGISTER_ACTIVITY = "register activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_marker)
        supportActionBar?.hide()

        getIntentData()
        initUI()
        initMapFragment()
        atachBtnLocationListener()
        attchAddressListener()
    }

    private fun initUI() {
        viewCanAddUpdate(false)
    }


    private fun getIntentData() {
        val reqStatus =
            intent.getStringExtra(
                EXTRA_FROM_ADD_UPDATE
            ) ?: intent.getStringExtra(
                EXTRA_FROM_REGISTER_ACTIVITY
            )
            ?: return


        val intentData = intent.getParcelableExtra<WiPlace>(EXTRA_PLACE)
        if (intentData != null) {
            place = intentData
        }

        activityCaller = reqStatus
    }

    private fun attchAddressListener() {
        btnShowAddress.setOnClickListener {
            viewCanAddUpdate()

            mapUtils.getAddress(getLocation) {
                place =
                    WiPlace(
                        place.name,
                        it,
                        getLocation.latitude,
                        getLocation.longitude,
                        place.description,
                        place.imagePath,
                        place.id
                    )

                updateUI(it)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = (googleMap ?: return)
        mapUtils = MapUtils(this.applicationContext, map)
        mapUtils.initMapMarker(R.drawable.ic_pin)
        mapUtils.initMarkerCameraListener { location ->
            getLocation = location
            place.locationLat = location.latitude
            place.locationLng = location.longitude
            place.address = ""
            Log.d(TAG, "initMarkerCameraListener ${getLocation.latitude}")



//            btnShowAddress.visibility = View.VISIBLE

            viewCanAddUpdate(false)

        }


    }

    private fun viewCanAddUpdate(isCan: Boolean = true) {

        if (isCan) {
            btnShowAddress.isEnabled = false
            btnGetLocation.isEnabled = true
        } else {
            btnGetLocation.isEnabled = false
            btnShowAddress.isEnabled = true

        }
    }

    private fun updateUI(address: String) {
        tvLocation.text = address

    }

    private fun initMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun atachBtnLocationListener() {
        btnGetLocation.setOnClickListener {
            success()
        }
    }

    private fun success() {

        val intent: Intent = when (activityCaller) {
            AddUpdateFragment::class.java.simpleName -> {
                Intent(baseContext, AddUpdateFragment::class.java)
            }
            else -> return
        }

        intent.putExtra(EXTRA_PLACE, place)
        setResult(RESULT_OK, intent)
        finish()
    }


}