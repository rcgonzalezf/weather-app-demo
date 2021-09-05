package org.rcgonzalezf.weather.common.models.db.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherInfoDao {

    @Query("DELETE FROM $WEATHER_INFO_TABLE")
    fun deleteOldWeatherInfo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherInfoEntity: WeatherInfoEntity)

    @Query("SELECT * FROM $WEATHER_INFO_TABLE ORDER BY $DATE_TIME ASC")
    fun getAll():List<WeatherInfoEntity>?

    companion object{
        const val WEATHER_INFO_TABLE = "WEATHER_ENTRIES"
        const val DATE_TIME = "DATE_TIME"
    }
}
