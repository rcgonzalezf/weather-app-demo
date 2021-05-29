package org.rcgonzalezf.weather.openweather.network

import org.rcgonzalezf.weather.common.network.RequestParameters
open // for Mockito
class OpenWeatherApiRequestParameters : RequestParameters {
    var lat: String? = null
    var lon: String? = null
    var cityName: String? = null
        private set

    private fun setCityName(cityName: String) {
        this.cityName = cityName
    }

    class OpenWeatherApiRequestBuilder {
        private val openWeatherApiRequestParameters: OpenWeatherApiRequestParameters
                = OpenWeatherApiRequestParameters()

        companion object {
            const val LON = "lon"
            const val LAT = "lat"
            const val TYPE = "type"
            const val LIKE = "like"
        }

        fun build(): OpenWeatherApiRequestParameters {
            return openWeatherApiRequestParameters
        }

        private fun withLat(lat: Double): OpenWeatherApiRequestBuilder {
            openWeatherApiRequestParameters.lat = lat.toString()
            return this
        }

        private fun withLon(lon: Double): OpenWeatherApiRequestBuilder {
            openWeatherApiRequestParameters.lon = lon.toString()
            return this
        }

        fun withLatLon(lat: Double, lon: Double): OpenWeatherApiRequestBuilder {
            return withLat(lat).withLon(lon)
        }

        fun withCityName(cityName: String): OpenWeatherApiRequestBuilder {
            openWeatherApiRequestParameters.setCityName(cityName)
            return this
        }
    }
}
