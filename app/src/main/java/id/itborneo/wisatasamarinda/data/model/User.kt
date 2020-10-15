package id.itborneo.wisatasamarinda.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String ="",
    val email: String ="",
    val photoUrl: String ="",
):Parcelable
