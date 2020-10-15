package id.itborneo.wisatasamarinda.di

import android.content.Context
import id.itborneo.wisatasamarinda.data.WiRepository
import id.itborneo.wisatasamarinda.data.local.LocalDataSource
import id.itborneo.wisatasamarinda.data.remote.RemoteDataSource
import id.itborneo.wisatasamarinda.data.remote.firebase.WiFirebase
import id.itborneo.wisatasamarinda.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): WiRepository {

        val wiFirebase = WiFirebase
        val localDataSource = LocalDataSource.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance(wiFirebase)
        val appExecutors = AppExecutors()


        return WiRepository.getInstance(remoteDataSource, localDataSource, appExecutors)


    }
}