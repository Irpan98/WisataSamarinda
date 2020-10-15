package id.itborneo.wisatasamarinda.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.itborneo.wisatasamarinda.data.local.database.WiDatatabase
import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import id.itborneo.wisatasamarinda.utils.AndroidStorageUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LocalDataSource(context: Context) {

    private val androidStorageUtils = AndroidStorageUtils(context)

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(context: Context): LocalDataSource =
            INSTANCE ?: LocalDataSource(context)
    }


    private val TAG = "LocalDataSource"
    private val dao = WiDatatabase.instance(context)

    private val placeDao = dao.placeDao()

    fun insertPlaces(places: List<WiPlaceEntity>) {

        placeDao.insertPlaces(places)

    }

    @SuppressLint("CheckResult")
    fun getPlaces(): MutableLiveData<List<WiPlaceEntity>> {

        val places = MutableLiveData<List<WiPlaceEntity>>()

        placeDao.getPlaces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
//                LiveDataReactiveStreams.fromPublisher(it)
                Log.d(TAG, " getPlaces ${it.size}")
                places.value = it
            }

        return places
    }

    @SuppressLint("CheckResult")
    fun insertOnePlace(place: WiPlaceEntity): MutableLiveData<Long> {
        val idResult = MutableLiveData<Long>()


        Observable.fromCallable { placeDao.insertOnePlace(place) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, it.toString())

                idResult.postValue(it)
            }, {
            })

        return idResult
    }

    @SuppressLint("CheckResult")
    fun editPlace(place: WiPlaceEntity): LiveData<Int> {
        val result = MutableLiveData<Int>()


        Observable.fromCallable { placeDao.editPlace(place) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, it.toString())

                result.postValue(it)
            }, {
            })

        return result
    }


    @SuppressLint("CheckResult")
    fun removePlace(place: WiPlaceEntity): LiveData<Int> {
        val result = MutableLiveData<Int>()


        Observable.fromCallable { placeDao.removePlace(place) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, it.toString())

                result.postValue(it)
            }, {
            })

        return result
    }
//    fun loadImage(pathImage: String){
//        androidStorageUtils.loadBitmap(pathImage)
//    }
//
//    fun saveImage(){
//        androidStorageUtils.saveFile()
//    }


}
