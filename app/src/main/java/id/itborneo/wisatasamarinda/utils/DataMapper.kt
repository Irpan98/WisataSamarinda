package id.itborneo.wisatasamarinda.utils

import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import id.itborneo.wisatasamarinda.data.model.WiPlace

object DataMapper {
    fun mapModelToEntity(input: WiPlace) =

        WiPlaceEntity(
            name = input.name,
            address = input.address,
            locationLat = input.locationLat,
            locationLng = input.locationLng,
            description = input.description,
            imagePath = input.imagePath,
            id = input.id
        )

    fun mapEntityToModel(input: WiPlaceEntity) =

        WiPlace(
            name = input.name,
            address = input.address,
            locationLat = input.locationLat,
            locationLng = input.locationLng,
            description = input.description,
            imagePath = input.imagePath,
            id = input.id
        )
}