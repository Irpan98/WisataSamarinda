package id.itborneo.wisatasamarinda.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class WiPlaceEntity(

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "locationLat")
    val locationLat: Double,

    @ColumnInfo(name = "locationLng")
    val locationLng: Double,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "imagePath")
    val imagePath: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String
)