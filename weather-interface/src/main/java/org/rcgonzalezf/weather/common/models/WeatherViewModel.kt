package org.rcgonzalezf.weather.common.models

import android.os.Parcelable
import java.io.Serializable

interface WeatherViewModel : Parcelable, Serializable {
    val id: Int
    val cityName: String?
    val speed: Double
    val humidity: String?
    val temperature: Double
    val dateTime: String?
    val weatherId: Int
    val country: String?
    val deg: Double
    val description: String?
}
