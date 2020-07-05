package org.rcgonzalezf.weather.common.listeners

import org.rcgonzalezf.weather.common.models.WeatherInfo

interface OnUpdateWeatherListListener {
    fun updateList(weatherInfoList: List<WeatherInfo>)
    fun onError(error: String)
}
