package org.rcgonzalezf.weather.common.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "weather_entries")
data class WeatherInfoEntity(
        @PrimaryKey
        val id: String = UUID.randomUUID().toString(),
        @ColumnInfo
        val cityId:Int = 0,
        @ColumnInfo
        val weatherId: Int = 0,
        @ColumnInfo
        val cityName: String? = null,
        @ColumnInfo
        val speed: Double = 0.0,
        @ColumnInfo
        val temperature: Double = 0.0,
        @ColumnInfo
        val humidity: String? = null,
        @ColumnInfo
        val dateTime: String? = null,
        @ColumnInfo
        val country: String? = null,
        @ColumnInfo
        val deg: Double = 0.0,
        @ColumnInfo
        var description: String? = null
)
