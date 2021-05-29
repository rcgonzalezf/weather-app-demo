package org.rcgonzalezf.weather.openweather.model

import org.rcgonzalezf.weather.common.models.converter.Data

class OpenWeatherCurrentData : Data {
    val name: String? = null
    val cod: String? = null
    val wind: Wind? = null
    val weather: List<Weather>? = null
    val sys: Sys? = null
    val dt: Long = 0
    val main: Main? = null
}
