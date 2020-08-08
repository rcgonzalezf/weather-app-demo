package org.rcgonzalezf.weather.common.network

interface ApiRequest<T : RequestParameters?, A : ApiCallback<out ApiResponse<*>?, out ApiError<*>?>?> {
    fun execute(apiCallback: A)
    fun addRequestParameters(requestParameters: T)
}
