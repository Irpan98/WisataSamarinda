package id.itborneo.wisatasamarinda.ui.wisata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import id.itborneo.wisatasamarinda.data.WiRepository
import id.itborneo.wisatasamarinda.data.model.WiPlace

class WisataViewModel(private val repo: WiRepository) : ViewModel() {

    //    val wisataPlaces = repo.getWisataPlaces()
    private val _refreshItems = MutableLiveData<Unit>()

    val wisataPlaces = Transformations.switchMap(_refreshItems) { repo.getWisataPlaces() }


    fun getImageUri(listPlace: List<WiPlace>) =
        repo.getAllImage(listPlace)

    fun deletePlace(wiPlace: WiPlace) =
        repo.deletePlace(wiPlace)

    fun getOneImage(id: String) =
        repo.getOneImage(id)


    fun onResume() {
        _refreshItems.value = Unit
    }

}
