package org.rcgonzalezf.weather.openweather.model

import com.google.gson.annotations.SerializedName

class WeatherData {
    var speed = 0.0
    var deg = 0.0
    var temp = 0.0
    var humidity: Long = 0

    @SerializedName("dt_txt")
    var dateTime: String? = null
    var weatherId = 0
    var description: String? = null
}
