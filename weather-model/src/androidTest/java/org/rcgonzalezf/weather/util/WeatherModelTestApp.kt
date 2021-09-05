package org.rcgonzalezf.weather.util

import okhttp3.OkHttpClient
import org.mockito.Mockito.mock
import org.rcgonzalezf.weather.WeatherLibApp

class WeatherModelTestApp : WeatherLibApp() {

    override fun createOkHttpClient(): OkHttpClient {
        return mock(OkHttpClient::class.java)
    }

    override fun addAnalyticsObservers() {
    }
}
