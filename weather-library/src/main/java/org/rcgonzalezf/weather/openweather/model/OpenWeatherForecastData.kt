package org.rcgonzalezf.weather.openweather.model

import com.google.gson.annotations.SerializedName
import org.rcgonzalezf.weather.common.models.converter.Data

class OpenWeatherForecastData : Data {
    val cnt: String? = null
    val cod: String? = null

    @SerializedName("list")
    val weatherList: List<WeatherList>? = null
    val city: City? = null
}
