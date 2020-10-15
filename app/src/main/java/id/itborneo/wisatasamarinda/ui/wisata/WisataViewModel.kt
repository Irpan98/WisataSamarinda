package id.itborneo.wisatasamarinda.ui.wisata

import androidx.lifecycle.ViewModel
import id.itborneo.wisatasamarinda.data.WiRepository
import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import id.itborneo.wisatasamarinda.data.model.WiPlace

class WisataViewModel(private val repo: WiRepository) : ViewModel() {

    val wisataPlaces = repo.getWisataPlaces()

    fun getImageUri(listPlace: List<WiPlace>) =
        repo.getAllImage(listPlace)

    fun deletePlace(wiPlace: WiPlace) =
        repo.deletePlace(wiPlace)


}
