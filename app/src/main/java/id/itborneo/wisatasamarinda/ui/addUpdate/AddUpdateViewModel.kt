package id.itborneo.wisatasamarinda.ui.addUpdate

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.itborneo.wisatasamarinda.data.WiRepository
import id.itborneo.wisatasamarinda.data.model.WiPlace

class AddUpdateViewModel(private val repo: WiRepository) : ViewModel() {

    fun addWiPlace(wiPlace: WiPlace): LiveData<String> {
        return repo.AddWiPlace(wiPlace)

    }

    fun editWiPlace(wiPlace: WiPlace): LiveData<Int> {
        return repo.editWiPlace(wiPlace)
    }

//    fun addImage(imageUri: String) {
//        repo.addImage(imageUri)
//    }

}