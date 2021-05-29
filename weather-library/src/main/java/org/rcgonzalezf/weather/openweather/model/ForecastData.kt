package org.rcgonzalezf.weather.openweather.model

import com.google.gson.annotations.SerializedName
import org.rcgonzalezf.weather.common.models.converter.Data
import java.util.ArrayList

open // open for Mockito
class ForecastData(@SerializedName("city") var city: City, count: Int) : Data {

    @SerializedName("cnt")
    var count = 0

    @SerializedName("list")
    private val weatherList: MutableList<WeatherData>
    override fun toString(): String {
        return "ForecastData [name=" + city.name + ", count=" + count + ""
    }

    fun getWeatherList(): List<WeatherData> {
        return weatherList
    }

    fun addWeatherItem(weatherData: WeatherData) {
        weatherList.add(weatherData)
    }

    init {
        this.count = count
        weatherList = ArrayList(count)
    }
}
