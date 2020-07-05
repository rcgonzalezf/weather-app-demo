package org.rcgonzalezf.weather.openweather.network

import androidx.annotation.VisibleForTesting
import org.rcgonzalezf.weather.common.models.converter.ModelConverter
import org.rcgonzalezf.weather.common.network.ApiRequest
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import org.rcgonzalezf.weather.openweather.converter.OpenWeatherApiModelConverter
import org.rcgonzalezf.weather.openweather.model.ForecastData
import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open // for Mockito
class OpenWeatherApiRequest @VisibleForTesting internal constructor(private val apiKey: String?,
                                                                    private val modelConverter: ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData>) : ApiRequest<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> {
    private var requestParameters: OpenWeatherApiRequestParameters? = null

    constructor(apiKey: String?) : this(apiKey, OpenWeatherApiModelConverter()) {}

    override fun execute(apiCallback: OpenWeatherApiCallback) {
        val mOpenWeatherExecutor = OpenWeatherExecutor(apiCallback, executor, apiKey)
        mOpenWeatherExecutor.setModelConverter(modelConverter)
        mOpenWeatherExecutor.performRetrofitCall(requestParameters)
    }

    override fun addRequestParameters(requestParameters: OpenWeatherApiRequestParameters) {
        this.requestParameters = requestParameters
    }

    // open for Mockito
    @get:VisibleForTesting
    open val executor: Executor
        get() = Executors.newSingleThreadExecutor()
}
