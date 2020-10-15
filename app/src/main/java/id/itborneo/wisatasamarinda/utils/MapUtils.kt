package id.itborneo.wisatasamarinda.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.util.*


class MapUtils(private val context: Context, private val map: GoogleMap) {
    private val TAG = "MapUtils"
    private lateinit var mapMarker: Marker


    fun getAddress(location: LatLng, fullAddressListener: (String) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())

//        doAsync {
        try {


            val getAddresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            Log.d(TAG, "getAddress: doAsync: $getAddresses")

//            uiThread {

            Log.d(TAG, "getAddress: doAsync:uiThread $getAddresses")
            val address: Address?
            val fulladdress: String
            if (getAddresses.isNotEmpty()) {
                address = getAddresses[0]
                fulladdress = address.getAddressLine(0)
                var city = address.locality
                var state = address.adminArea
                var country = address.countryName
                var postalCode = address.postalCode
                var knownName = address.featureName
            } else {
                fulladdress = "Location not found"
            }
            fullAddressListener(fulladdress)

//            }
//        }
        }catch (e: Exception){
            Toast.makeText(context,"Jaringan Bermasalah. Silahkan Klik 'Location'",Toast.LENGTH_LONG ).show()
            fullAddressListener("Indonesia")

            Log.d(TAG,"getAddress error $e")
        }


    }

    fun initMapMarker(icon: Int = 0, position: LatLng = LatLng(-0.502106, 117.153709)) {

        val ic: BitmapDescriptor = if (icon == 0) {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        } else {
            getBitmapDescriptor(context, icon) ?: return
        }


        mapMarker = map.addMarker(

            MarkerOptions()
                .position(position)
                .draggable(true)
                .icon(ic)
                .title("Outlite Location")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f))
    }

    private fun getBitmapDescriptor(context: Context, id: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable?
        vectorDrawable =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                context.getDrawable(id)
            } else {
                ContextCompat.getDrawable(context, id)
            }
        if (vectorDrawable != null) {
            val w = vectorDrawable.intrinsicWidth
            val h = vectorDrawable.intrinsicHeight

            vectorDrawable.setBounds(0, 0, w, h)
            val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bm)
            vectorDrawable.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bm)
        }
        return null
    }

    fun initMapMarkers(latLngs: MutableList<LatLng>, locations: MutableList<String>, icon: Int) {

        val ic = getBitmapDescriptor(context, icon)
        Log.d(TAG, "initMapMarkers called: $latLngs")
        for (index in 0 until latLngs.size) {
            mapMarker = map.addMarker(
                MarkerOptions()
                    .position(latLngs[index])
                    .draggable(false)
                    .icon(ic)
                    .title(locations[index])
            )
        }

    }

    fun initMarkerCameraListener(listener: (LatLng) -> Unit) {
        map.setOnCameraMoveListener {
//            DelayUtils.delay(200) {
            val location = map.cameraPosition.target
            setMarkerPosition(location)
            listener(location)
//            }


        }
    }


    private fun setMarkerPosition(latLng: LatLng) {
//        Log.d(TAG, "setMarkerPosition latlng $latLng")
        mapMarker.position = latLng
    }

//    class AsyncGeocoder(context: Context?) {
//        private val geocoder: Geocoder
//        fun reverseGeocode(lat: Double, lng: Double, callback: Callback): Disposable {
//            return Observable.fromCallable {
//                try {
//                    return geocoder.getFromLocation(lat, lng, 1)
//                } catch (e: Exception) {
////                    AppLogger.d("throwable,", Gson().toJson(e))
//                    e.printStackTrace()
//                }
//                false
//            }.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result ->
//                    //Use result for something
////                    AppLogger.d("throwable,", Gson().toJson(result))
//                    callback.success(result[0] as Address)
//                }) {
////                        throwable -> AppLogger.d("throwable,", Gson().toJson(throwable))
//                }
//        }
//
//        interface Callback {
//            fun success(address: Address?)
//            fun failure(e: Throwable?)
//        }
//
//        init {
//            geocoder = Geocoder(context)
//        }
//    }

}