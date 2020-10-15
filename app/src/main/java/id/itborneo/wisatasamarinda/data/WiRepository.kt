package id.itborneo.wisatasamarinda.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import id.itborneo.wisatasamarinda.data.local.LocalDataSource
import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import id.itborneo.wisatasamarinda.data.model.WiPlace
import id.itborneo.wisatasamarinda.data.remote.RemoteDataSource
import id.itborneo.wisatasamarinda.data.remote.firebase.ApiResponse
import id.itborneo.wisatasamarinda.utils.AppExecutors
import id.itborneo.wisatasamarinda.utils.DataMapper
import id.itborneo.wisatasamarinda.utils.DataMappers

class WiRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors,
) {
    private val TAG = "WiRepository"


    companion object {

        @Volatile
        private var instance: WiRepository? = null


        fun getInstance(
            remoteData: RemoteDataSource,
            localData: LocalDataSource,
            appExecutors: AppExecutors
        ): WiRepository =
            instance ?: WiRepository(
                remoteData, localData, appExecutors
            )

    }

    fun getWisataPlaces() =
        object : NetworkBoundResource<List<WiPlaceEntity>, List<WiPlace>>(appExecutors) {
            override fun loadFromDB(): LiveData<List<WiPlaceEntity>> {
                return localDataSource.getPlaces()
            }

            override fun shouldFetch(data: List<WiPlaceEntity>?): Boolean {
                Log.d(TAG, "shouldFetch: ")
//                return data == null || data.isEmpty()
                return true
            }

            override fun createCall(): LiveData<ApiResponse<List<WiPlace>>> {
                Log.d(TAG, "createCall: ")
                return remoteDataSource.getWiPlaces()
            }

            override fun saveCallResult(data: List<WiPlace>) {
                Log.d(TAG, "saveCallResult: ")

                localDataSource.insertPlaces(DataMappers.mapModelToEntitas(data))
            }

        }.asLiveData()


    fun AddWiPlace(wiPlace: WiPlace) =

        remoteDataSource.addWiPlace(wiPlace).map {
            val wPlaceNew = wiPlace
            wPlaceNew.id = it
            roomAddWiPlace(wPlaceNew)
            Log.d(TAG, "AddWiPlace transform")
            it
        }


    private fun roomAddWiPlace(wiPlace: WiPlace): MutableLiveData<Long> {
        Log.d(TAG, "roomAddWiPlace $wiPlace")
        var response = MutableLiveData<Long>()
        appExecutors.diskIO().execute {
            response = localDataSource.insertOnePlace(DataMapper.mapModelToEntity(wiPlace))
        }
        return response

    }

    fun editWiPlace(wiPlace: WiPlace) =

        remoteDataSource.updateWiPlace(wiPlace).map {
            roomEditPlace(wiPlace)
            it
        }


    private fun roomEditPlace(wiPlace: WiPlace) {
        Log.d(TAG, "roomAddWiPlace $wiPlace")
        appExecutors.diskIO().execute {
            localDataSource.editPlace(DataMapper.mapModelToEntity(wiPlace))
        }
    }


    fun getAllImage(id: List<WiPlace>) =
        remoteDataSource.getAllImage(DataMappers.mapModelToEntitas(id))


    fun deletePlace(wiPlace: WiPlace) =

        remoteDataSource.delete(wiPlace.id).map {
            roomDeletePlace(wiPlace)
            it
        }


    private fun roomDeletePlace(wiPlace: WiPlace) {
        localDataSource.removePlace(DataMapper.mapModelToEntity(wiPlace))

    }
}