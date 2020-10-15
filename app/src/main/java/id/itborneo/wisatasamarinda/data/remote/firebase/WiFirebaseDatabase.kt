package id.itborneo.wisatasamarinda.data.remote.firebase

import com.google.firebase.database.FirebaseDatabase

class WiFirebaseDatabase(firebase: FirebaseDatabase) {
    private val wiDatabaseRef = firebase.reference.child("wisataSamarinda")
    val wiPlaceChild = wiDatabaseRef.child("place")


}