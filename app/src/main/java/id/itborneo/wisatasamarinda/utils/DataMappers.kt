package id.itborneo.wisatasamarinda.utils

import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import id.itborneo.wisatasamarinda.data.model.WiPlace

object DataMappers {

    fun mapModelToEntitas(input: List<WiPlace>): List<WiPlaceEntity> =
        input.map {
            WiPlaceEntity(
                name = it.name,
                address = it.address,
                locationLat = it.locationLat,
                locationLng = it.locationLng,
                description = it.description,
                imagePath = it.imagePath,
                id = it.id
            )
        }

    fun mapEntitasToModel(input: List<WiPlaceEntity>): List<WiPlace> =
        input.map {
            WiPlace(
                name = it.name,
                address = it.address,
                locationLat = it.locationLat,
                locationLng = it.locationLng,
                description = it.description,
                imagePath = it.imagePath,
                id = it.id
            )
        }


//    fun map
}