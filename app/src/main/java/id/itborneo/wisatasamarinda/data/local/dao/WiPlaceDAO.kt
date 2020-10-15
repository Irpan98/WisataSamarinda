package id.itborneo.wisatasamarinda.data.local.dao

import androidx.room.*
import id.itborneo.wisatasamarinda.data.local.entity.WiPlaceEntity
import io.reactivex.Flowable


@Dao
interface WiPlaceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaces(wiplace: List<WiPlaceEntity>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOnePlace(wiplace: WiPlaceEntity): Long


    @Query("SELECT * FROM WiPlaceEntity")
    fun getPlaces(): Flowable<List<WiPlaceEntity>>


    @Update(entity = WiPlaceEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun editPlace(item: WiPlaceEntity): Int

    @Delete
    fun removePlace(item: WiPlaceEntity): Int
}
