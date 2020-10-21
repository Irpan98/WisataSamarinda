package id.itborneo.wisatasamarinda.ui.addUpdate

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.di.ViewModelFactory
import id.itborneo.wisatasamarinda.ui.mapMarker.MapMarkerActivity
import id.itborneo.wisatasamarinda.ui.mapMarker.MapMarkerActivity.Companion.EXTRA_FROM_ADD_UPDATE
import id.itborneo.wisatasamarinda.ui.mapMarker.MapMarkerActivity.Companion.REQ_MAP
import id.itborneo.wisatasamarinda.utils.*
import kotlinx.android.synthetic.main.dialog_choose_image.view.*
import kotlinx.android.synthetic.main.fragment_add_update.*
import kotlinx.android.synthetic.main.partial_loading.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddUpdateFragment : Fragment() {

    private lateinit var viewModel: AddUpdateViewModel
    private val TAG = "AddUpdateFragment"
    private lateinit var wiPlace: WiPlace
    private lateinit var imageUri: Uri
    private var reqStatus = "Add"
    private lateinit var navController: NavController

    companion object {
        const val EXTRA_REQ = "extra_req"
        const val REQ_STATUS_ADD = "add"
        const val REQ_STATUS_UPDATE = "update"

        private val MAP_MARKER_CODE = 1100

        private val IMAGE_PICK_CODE = 1000
        private val IMAGE_TAKE_CODE = 1011


//        private val PERMISSION_CODE = 1001

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViewModel()
        initBottomBar()
        initNavController(view)
        initAddUpdateListener()
        initAddLocationListener()
        initImageListener()
    }

    private fun initAddLocationListener() {
        btnGetLocation.setOnClickListener {




            getEditDataFilled()
            if (!isInternetAvailable()) return@setOnClickListener
            actionToMapsMarkerActivity()
        }
    }

    private fun actionToMapsMarkerActivity() {
        val intent = Intent(requireContext(), MapMarkerActivity::class.java)
        intent.putExtra(EXTRA_FROM_ADD_UPDATE, this.javaClass.simpleName)
        intent.putExtra(EXTRA_PLACE, wiPlace)
        startActivityForResult(intent, REQ_MAP)
    }


    private fun initData() {

        val intentPlace = arguments?.getParcelable<WiPlace>(EXTRA_PLACE)
        wiPlace = intentPlace ?: WiPlace("", "", 0.0, 0.0, "", "")

        Log.d(TAG, "initData" + wiPlace.toString())
        if (wiPlace.name != "") {
//            imageUri = wiPlace.imagePath.toUri()
            reqStatus = "Update"
        }
        updateUI()
    }

    private fun initImageListener() {
        ivPlace.setOnClickListener {
            getEditDataFilled()
            if (!isInternetAvailable()) return@setOnClickListener
            showDialog()
        }
    }

    private fun actionImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, IMAGE_PICK_CODE)
            } else {
                //permission already granted
                pickImageFromGallery()
            }
        } else {
            //system OS is < Marshmallowad
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun initBottomBar() {
        BottomNavUtils.invisible(activity)
    }

    private fun initAddUpdateListener() {

        btnAddUpdate.setOnClickListener {
            if (!isInternetAvailable()) return@setOnClickListener
            //Null checker
            Log.d(TAG, "validate Input ${isInputValid()}")
            if (isInputValid()) return@setOnClickListener


            btnAddUpdate.isEnabled = false

            loading()
            wiPlace = WiPlace(
                etName.text.toString(),
                etAddress.text.toString(),
                wiPlace.locationLat,
                wiPlace.locationLng,
                etDescription.text.toString(),
                wiPlace.imagePath,
                wiPlace.id
            )
            if (reqStatus == "Add") {
                actionAddItem()

            } else {
                actionEditItem()
            }
        }
    }

    private fun actionEditItem() {


        viewModel.editWiPlace(wiPlace).observe(requireActivity(), {
            loading(false)
            showToast("Berhasil Update Data")

            if (it == 1) {
                actionSuccess()
            }
        })


    }

    private fun isInputValid(): Boolean {
        var isImageUriNotInitialize = !::imageUri.isInitialized

        val tidakBoleh = "tidak boleh kosong"

        val isPlaceNull = ValidateInput.isEditNull(etName, "place $tidakBoleh")
        val isLocationNull = ValidateInput.isEditNull(etAddress, "Klik Tombol diatas")
        val isDescriptionNull = ValidateInput.isEditNull(etDescription, "Deskripsi $tidakBoleh")
        if (isLocationNull) {
            showToast("Klik Tombol Location")
        } else if (isImageUriNotInitialize) {
            if (wiPlace.imagePath == "") {
                showToast("Gambar tidak boleh Kosong, Silahkan Klik Gambar")

            } else {
                isImageUriNotInitialize = false
            }
        }


        return isPlaceNull ||
                isLocationNull ||
                isDescriptionNull ||
                isImageUriNotInitialize

    }

    private fun actionAddItem() {

        viewModel.addWiPlace(wiPlace).observe(requireActivity()) {
            loading(false)
            showToast("Berhasil Tambah Data")

            Log.d(TAG, "AddWiPlace: ${it}")
            actionSuccess()
        }
    }

    private fun actionSuccess() {
        navController.navigate(R.id.action_addUpdateFragment_to_wisataPlaceFragment)
    }

    private fun initNavController(view: View) {
        navController = Navigation.findNavController(view)
    }


    private fun initViewModel() {

        val factory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[AddUpdateViewModel::class.java]
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            IMAGE_PICK_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            IMAGE_TAKE_CODE -> {


                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    actionOpenCamera()

                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }



        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult   ")

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            data?.data?.let {
                imageUri = it
                Log.d(TAG, "onActivityResult ${data.data}")
                dialog.dismiss()
                updateUI()

//                viewModel.addImage(it.toString())
                ivPlace.setImageURI(data.data)
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQ_MAP) {
            Log.d(TAG, "onActivityResult getMap ${data?.data}")
            if (data == null) return
            val intentOutlite = data.getParcelableExtra<WiPlace>(EXTRA_PLACE) ?: return
            Log.d(TAG, "onActivityResult getMap $intentOutlite")


            wiPlace.locationLng = intentOutlite.locationLng
            wiPlace.locationLat = intentOutlite.locationLat
            wiPlace.address = intentOutlite.address

            updateUI()
        } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_TAKE_CODE) {
            Log.d(TAG, "onActivityResult IMAGE_TAKE_CODE   ")
            dialog.dismiss()

            val auxFile = File(mCurrentPhotoPath)
            imageUri = Uri.fromFile(auxFile)
//            wiPlace.imagePath = imageUri.toString()


            val bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            ivPlace.setImageBitmap(bitmap)

//            data?.data?.let {
//                imageUri = it
//                Log.d(TAG, "onActivityResult ${data.data}")
////                updateUI()
//
////                viewModel.addImage(it.toString())
//                ivPlace.setImageURI(data.data)
//            }


        }
    }

    private fun updateUI() {
        etName.setText(wiPlace.name)
        etDescription.setText(wiPlace.description)
        etAddress.setText(wiPlace.address)
        etLatLng.setText(wiPlace.locationLat.toString() + "," + wiPlace.locationLng)
        if (::imageUri.isInitialized) {
            // image from add new image
            wiPlace.imagePath = imageUri.toString()
        } else {
            //image from detailFragment or wisataPlaceFragment
            if (wiPlace.imagePath != "") {
//                imageUri = "".toUri()

                Glide.with(requireContext())
                    .load(wiPlace.imagePath)
                    .fitCenter()
                    .placeholder(requireContext().getDrawable(R.drawable.loading_image))
                    .into(ivPlace)
            }
        }


        if (reqStatus != "Add") {
            btnAddUpdate.text = getString(R.string.edit)
            btnAddUpdate.icon = getDrawable(requireContext(), R.drawable.ic_edit)
        }


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

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun loading(showLoading: Boolean = true) {
        if (showLoading) {
            SpinKitUtils.show(spinKitLoading)
        } else {
            SpinKitUtils.hide(spinKitLoading)
        }
    }

    private lateinit var dialog: AlertDialog

    private fun showDialog() {
        val window = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_choose_image, null)
        window.setView(view)

        view.btnCamera.setOnClickListener {

            actionOpenCamera()

        }
        view.btnGallery.setOnClickListener {

            actionImagePicker()
//            val mimeType = arrayOf("images/jpg", "images/jpeg")
//            val intent = Intent()
//            intent.type = "*/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//            startActivityForResult(Intent.createChooser(intent, "chooseImage"), GALLERY_MODE)
        }
        dialog = window.create()
        dialog.show()

    }

    private fun actionOpenCamera() {
        if (cameraCheckPersmission()) openCamera()
        else requestPermission()


    }

    private fun cameraCheckPersmission(): Boolean {
        return (checkSelfPermission(requireContext(), CAMERA) ==
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            IMAGE_TAKE_CODE
        )
    }

    private fun openCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "id.itborneo.wisatasamarinda.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, IMAGE_TAKE_CODE)
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, IMAGE_TAKE_CODE)
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    private var mCurrentPhotoPath: String? = null;

    private fun getEditDataFilled() {
        wiPlace.name = etName.text.toString()
        wiPlace.description = etDescription.text.toString()
    }


}