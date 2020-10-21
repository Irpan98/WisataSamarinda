package id.itborneo.wisatasamarinda.ui.mapMarker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
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

        if (!locationPermissionGranted) { //if locattion not granted, requesit and return

            getLocationPermission()
        }
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
        getDeviceLocation()


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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    getDeviceLocation()

                } else {
                    success()

                }
            }
        }
    }

    // Construct a FusedLocationProviderClient.
//    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult =
                    LocationServices.getFusedLocationProviderClient(this).lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.

                        val lastKnownLocation = task.result
                        mapUtils.initMapMarker(
                            R.drawable.ic_pin,
                            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                        )

//                        if (lastKnownLocation != null) {
//                            map.moveCamera(
//                                CameraUpdateFactory.newLatLngZoom(
//                                    LatLng(
//                                        lastKnownLocation!!.latitude,
//                                        lastKnownLocation!!.longitude
//                                    ), DEFAULT_ZOOM.toFloat()
//                                )
//                            )
//                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mapUtils.initMapMarker(R.drawable.ic_pin)

                        map.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1021
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

}