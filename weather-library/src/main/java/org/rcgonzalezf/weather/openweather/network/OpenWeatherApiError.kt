package org.rcgonzalezf.weather.openweather.network

import org.rcgonzalezf.weather.common.network.ApiError

class OpenWeatherApiError : ApiError<String?> {
    override var error: String? = null
        private set
    private var code: ErrorCode? = null
    fun setMessage(message: String?) {
        error = message
    }

    override fun setCode(code: ErrorCode?) {
        this.code = code
    }

    fun getCode(): ErrorCode? {
        return code
    }
}
