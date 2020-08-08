package org.rcgonzalezf.weather.common.network

import org.rcgonzalezf.weather.openweather.network.ErrorCode

interface ApiError<T> {
    val error: T
    fun setCode(code: ErrorCode?)
}
