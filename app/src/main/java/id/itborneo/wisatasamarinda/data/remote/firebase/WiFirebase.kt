package id.itborneo.wisatasamarinda.data.remote.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object WiFirebase {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val WiFirebaseDatabase = WiFirebaseDatabase(firebaseDatabase)

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val wiFirebaseStorage = WiFirebaseStorage(firebaseStorage)

    fun database(): WiFirebaseDatabase {
        return WiFirebaseDatabase
    }

    fun storage(): WiFirebaseStorage {
        return wiFirebaseStorage
    }

}