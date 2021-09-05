package org.rcgonzalezf.weather.common.models.db.weather

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.rcgonzalezf.weather.common.models.db.weather.WeatherInfoDao.Companion.DATE_TIME
import org.rcgonzalezf.weather.common.models.db.weather.WeatherInfoDao.Companion.WEATHER_INFO_TABLE
import java.util.UUID

@Entity(tableName = WEATHER_INFO_TABLE)
data class WeatherInfoEntity(
        @PrimaryKey
        var id: String = UUID.randomUUID().toString(),
        @ColumnInfo
        var cityId:Int = 0,
        @ColumnInfo
        var weatherId: Int = 0,
        @ColumnInfo
        var cityName: String? = null,
        @ColumnInfo
        var speed: Double = 0.0,
        @ColumnInfo
        var temperature: Double = 0.0,
        @ColumnInfo
        var humidity: String? = null,
        @ColumnInfo(name = DATE_TIME)
        var dateTime: String? = null,
        @ColumnInfo
        var country: String? = null,
        @ColumnInfo
        var deg: Double = 0.0,
        @ColumnInfo
        var description: String? = null
)
