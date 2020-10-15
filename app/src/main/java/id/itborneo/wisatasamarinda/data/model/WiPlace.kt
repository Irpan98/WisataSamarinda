package id.itborneo.wisatasamarinda.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WiPlace(
    var name: String= "",
    var address: String= "",
    var locationLat: Double= 0.0,
    var locationLng: Double= 0.0,
    var description: String= "",
    var imagePath: String="",
    var id: String =""
):Parcelable