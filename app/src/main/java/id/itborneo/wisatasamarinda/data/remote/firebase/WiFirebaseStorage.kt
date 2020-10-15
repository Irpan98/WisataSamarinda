package id.itborneo.wisatasamarinda.data.remote.firebase

import com.google.firebase.storage.FirebaseStorage

class WiFirebaseStorage(firebase: FirebaseStorage) {

    private val storageReference = firebase.reference.child("wisataSamarinda")
    val PlaceImage = storageReference.child("place")


}