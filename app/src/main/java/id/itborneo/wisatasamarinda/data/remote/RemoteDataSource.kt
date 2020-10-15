package id.itborneo.wisatasamarinda.data.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.data.remote.firebase.ApiResponse
import id.itborneo.wisatasamarinda.data.remote.firebase.WiFirebase


class RemoteDataSource(wiFirebase: WiFirebase) {
    private val TAG = "RemoteDataSource"


    companion object {

        @Volatile
        private var instance: RemoteDataSource? = null


        fun getInstance(wiFirebase: WiFirebase): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(wiFirebase)
            }

    }


    val database = wiFirebase.database()
    val storage = wiFirebase.storage()

    fun getWiPlaces(): LiveData<ApiResponse<List<WiPlace>>> {
        Log.d(TAG, "getWiPlaces: called")

        val result = MutableLiveData<ApiResponse<List<WiPlace>>>()

        database.wiPlaceChild.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "getWiPlaces:${snapshot.value.toString()} ")

                val listPlaces = mutableListOf<WiPlace>()
                snapshot.children.forEach { dataSnapshot ->
                    val wiPlace = dataSnapshot.getValue(WiPlace::class.java)
                    if (wiPlace != null) {
                        wiPlace.id = dataSnapshot.key ?: ""
                        listPlaces.add(wiPlace)
                    } else {
//                        result.postValue(ApiResponse.Empty)
                    }
                }
                result.postValue(ApiResponse.Success(listPlaces))

            }

            override fun onCancelled(error: DatabaseError) {
//                result.postValue(ApiResponse.Error("getWisataPlaces error $error"))

            }

        })

        return result
    }

    fun addWiPlace(place: WiPlace): MutableLiveData<String> {
        Log.d(TAG, "addWiPlace: called")
        var response = MutableLiveData<String>()
        val push = database.wiPlaceChild.push()
        val key = push.key!!
        place.id = key

        push.setValue(place)
            .addOnSuccessListener {
                Log.d(TAG, "addWiPlace: setValue,addOnSuccessListener")


                val file: Uri = Uri.parse(place.imagePath)
                val riversRef: StorageReference =
                    storage.PlaceImage.child(key)
                val uploadTask = riversRef.putFile(file)

                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "addImageToStorage, addOnFailureListener")
                }.addOnSuccessListener {
                    response.postValue("1")

                    Log.d(TAG, "addImageToStorage, addOnSuccessListener")
                    // ...
                }

//                Transformations.map(addImageToStorage(place.imagePath, key)){
//                    Log.d(TAG, "addWiPlace: setValue,addOnSuccessListener addImageToStorage map")
//
//                    response.postValue(it)
//                }

//                addImageToStorage(place.imagePath, key).map {
//
//                }


//                response = addImageToStorage(place.imagePath, key)


            }.addOnFailureListener {
                Log.d(TAG, "addWiPlace: setValue,addOnFailureListener")

                response.postValue(null)
            }

        return response
    }

    fun updateWiPlace(wiPlace: WiPlace): MutableLiveData<Int> {
        Log.d(TAG, "updateWiPlace $wiPlace")

        val response = MutableLiveData<Int>()
        val dbPlaceChild = database.wiPlaceChild
        dbPlaceChild.child(wiPlace.id).setValue(wiPlace)
            .addOnSuccessListener {
                response.value =1

            }.addOnFailureListener {
                Log.d("FirebaseServicesOutlite", "addOnFailureListener ${it.message}")
                response.postValue(0)
            }

        return response
    }

    fun delete(key: String): MutableLiveData<Int> {


        val response = MutableLiveData<Int>()

        Log.d(TAG, "delete called")
        database.wiPlaceChild.child(key).setValue(null)
            .addOnSuccessListener {
                response.postValue(1)

            }.addOnFailureListener {
                Log.d("delete", "addOnFailureListener ${it.message}")
                response.postValue(0)
            }

        return response


    }


    private fun addImageToStorage(imagePathName: String, key: String): LiveData<String> {

        val response = MutableLiveData<String>()

//                val file: Uri = Uri.fromFile(File("path/to/images/rivers.jpg"))

        val file: Uri = Uri.parse(imagePathName)
        val riversRef: StorageReference =
            storage.PlaceImage.child(key)
        val uploadTask = riversRef.putFile(file)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(TAG, "addImageToStorage, addOnFailureListener")
        }.addOnSuccessListener {
            response.postValue("1")

            Log.d(TAG, "addImageToStorage, addOnSuccessListener")
            // ...
        }

        return response

    }


    fun getAllImage(allId: List<WiPlaceEntity>): LiveData<List<Uri>> {
        Log.d(TAG, "getAllImage called")

        var result = MutableLiveData<List<Uri>>()
        val imagePaths = mutableListOf<Uri>()


        allId.forEach { i ->
            Log.d(TAG, "getAllImage allId $i")

            storage.PlaceImage.child(i.id).downloadUrl.addOnSuccessListener { uri ->
                imagePaths.add(uri)
                result.postValue(imagePaths)
                Log.d(TAG, "getImageFromStorage$uri")
//                result.postValue(uri)
            }.addOnFailureListener {
                Log.d(TAG, " getImageFromStorage $it")
                // Handle any errors
            }

//            getImageFromStorage(i.id).map {
//                Log.d(TAG, "getAllImage $imagePaths")
//
//                //add uri to list
//                imagePaths.add(it)
//            }
        }
//        result.postValue(imagePaths)

        return result

    }


//    private fun getImageFromStorage(id: String): LiveData<Uri> {

//        val ref = storage.PlaceImage.child(id)
//
//        val localFile: File = File.createTempFile ("images", "jpg")
//
//        ref.getFile(localFile)
//            .addOnSuccessListener {
//                // Local temp file has been created
//            }.addOnFailureListener {
//                // Handle any errors
//            }


//        var result = MutableLiveData<Uri>()
//        storage.PlaceImage.child(id).downloadUrl.addOnSuccessListener { uri ->
//            Log.d(TAG, "getImageFromStorage$uri")
//            result.postValue(uri)
//        }.addOnFailureListener {
//            Log.d(TAG, " getImageFromStorage $it")
//            // Handle any errors
//        }
//        return result
//    }


}