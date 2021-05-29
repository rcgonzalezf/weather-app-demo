package rcgonzalezf.org.weather.common

import org.rcgonzalezf.weather.common.models.WeatherInfo

interface OnOfflineLoader {
    fun loadOldData(weatherInfoList: List<WeatherInfo>?)
}
