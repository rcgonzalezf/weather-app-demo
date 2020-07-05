package org.rcgonzalezf.weather.common.models.converter

import java.io.IOException

interface ModelConverter<D : Data?, E, W : Data?> {
    fun fromForecastPojo(pojo: D)
    fun fromWeatherPojo(pojo: W)

    @get:Throws(IOException::class)
    val forecastModel: List<E>?

    @get:Throws(IOException::class)
    val weatherModel: List<E>?
}
