package id.itborneo.wisatasamarinda.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import id.itborneo.wisatasamarinda.data.remote.Resource
import id.itborneo.wisatasamarinda.data.remote.firebase.ApiResponse
import id.itborneo.wisatasamarinda.utils.AppExecutors

abstract class NetworkBoundResource<ResultType, RequestType>(private val mExecutors: AppExecutors) {
    private val TAG = "NetworkBoundResource"
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.Loading(null)
        Log.d(TAG, ": init called")
        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            Log.d(TAG, ":  result.addSource(dbSource) called")

            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    result.value = Resource.Success(newData)
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): LiveData<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    protected abstract fun saveCallResult(data: RequestType)

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        Log.d(TAG, ":  fetchFromNetwork called")

        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            Log.d(TAG, ":  result.addSource(dbSource) called")

            result.value = Resource.Loading(newData)
        }
        result.addSource(apiResponse) { response ->
            Log.d(TAG, ":    result.addSource(apiResponse called")

            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiResponse.Success ->
                    mExecutors.diskIO().execute {
                        saveCallResult(response.data)
                        mExecutors.mainThread().execute {
                            result.addSource(loadFromDB()) { newData ->
                                result.value = Resource.Success(newData)
                            }
                        }
                    }
                is ApiResponse.Empty -> mExecutors.mainThread().execute {
                    result.addSource(loadFromDB()) { newData ->
                        result.value = Resource.Success(newData)
                    }
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        result.value = Resource.Error(response.errorMessage, newData)
                    }
                }

            }
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> = result
}