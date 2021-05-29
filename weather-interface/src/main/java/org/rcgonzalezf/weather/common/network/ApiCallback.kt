package org.rcgonzalezf.weather.common.network

interface ApiCallback<T : ApiResponse<*>?, E : ApiError<*>?> {
    fun onSuccess(apiResponse: T)
    fun onError(apiError: E)
}
