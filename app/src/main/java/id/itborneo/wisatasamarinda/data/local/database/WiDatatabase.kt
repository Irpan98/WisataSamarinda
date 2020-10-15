package id.itborneo.wisatasamarinda.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.itborneo.wisatasamarinda.data.local.dao.WiPlaceDAO
import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity


@Database(version = 1, entities = [WiPlaceEntity::class])
abstract class WiDatatabase : RoomDatabase() {

    abstract fun placeDao(): WiPlaceDAO


    companion object {
        private var INSTANCE: WiDatatabase? = null

        @JvmStatic
        fun instance(context: Context): WiDatatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WiDatatabase::class.java, "db_movie_catalogue"
                ).build()
            }
            return INSTANCE as WiDatatabase
        }
    }

}