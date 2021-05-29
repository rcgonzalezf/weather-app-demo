package org.rcgonzalezf.weather.openweather.network

import org.rcgonzalezf.weather.common.models.converter.Data
import org.rcgonzalezf.weather.common.network.ApiResponse
open // for Mockito
class OpenWeatherApiResponse<D : Data?> : ApiResponse<D> {
    override var data: List<D>? = null
}
