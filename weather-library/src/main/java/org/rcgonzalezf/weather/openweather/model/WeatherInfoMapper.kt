package org.rcgonzalezf.weather.openweather.model

import androidx.annotation.VisibleForTesting
import org.rcgonzalezf.weather.common.models.WeatherInfo
import java.util.ArrayList

class WeatherInfoMapper {

    companion object {
        private const val ALL = -1

        // I've found that most cases the forecast consists of 40 items
        private const val INITIAL_SIZE = 40
    }

    private var data: List<ForecastData>? = null
    fun withData(data: List<ForecastData>): WeatherInfoMapper {
        this.data = data
        return this
    }

    fun map(): List<WeatherInfo> {
        return map(ALL)
    }

    @VisibleForTesting
    fun map(howMany: Int): List<WeatherInfo> {
        var size = 10
        if (howMany == ALL) {
            size = INITIAL_SIZE * data!!.size
        }
        var counter = 0
        val weatherInfoList: MutableList<WeatherInfo> = ArrayList(size)
        for (forecastData in data!!) {
            for (weather in forecastData.getWeatherList()) {
                val weatherInfo = WeatherInfo()
                weatherInfo.setCityId(forecastData.city.id)
                weatherInfo.setCityName(forecastData.city.name)
                weatherInfo.setSpeed(weather.speed)
                weatherInfo.setTemperature(weather.temp)
                weatherInfo.setHumidity(weather.humidity.toString())
                weatherInfo.setDateTime(weather.dateTime)
                weatherInfo.setWeatherId(weather.weatherId)
                weatherInfo.setCountry(
                        if (forecastData.city.country == null) "" else forecastData.city.country)
                weatherInfo.setDeg(weather.deg)
                weatherInfo.setDescription(weather.description)
                weatherInfoList.add(weatherInfo)
                ++counter
                if (counter == howMany) {
                    return weatherInfoList
                }
            }
        }
        return weatherInfoList
    }
}
