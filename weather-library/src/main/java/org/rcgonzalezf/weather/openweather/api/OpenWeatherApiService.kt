package org.rcgonzalezf.weather.openweather.api

import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApiService {

    companion object {
        const val APP_ID = "APPID"
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }

     /*
      Forecast for 5 days, more info at: https://openweathermap.org/forecast5
    */
    // "https://api.openweathermap.org/data/2.5/forecast?lon=139.0&lat=35.0&APPID=someApiKey",
    @GET("forecast")
    fun findForecastFiveDaysByLatLon(@Query(OpenWeatherApiRequestBuilder.LAT) lat: String?,
                                     @Query(OpenWeatherApiRequestBuilder.LON) lon: String?, @Query(APP_ID) appId: String?): Call<OpenWeatherForecastData?>?

    // "https://api.openweathermap.org/data/2.5/forecast?q=London&type=like&APPID=someApiKey",
    @GET("forecast")
    fun findForecastFiveDaysByQuery(@Query(value = "q", encoded = true) query: String?,
                                    @Query(OpenWeatherApiRequestBuilder.TYPE) type: String?, @Query(APP_ID) appId: String?): Call<OpenWeatherForecastData?>?

     /*
      Current weather more info at: https://openweathermap.org/current
    */
    // "https://api.openweathermap.org/data/2.5/weather?lon=139.0&lat=35.0&APPID=someApiKey",
    @GET("weather")
    fun findWeatherByLatLon(@Query(OpenWeatherApiRequestBuilder.LAT) lat: String?,
                            @Query(OpenWeatherApiRequestBuilder.LON) lon: String?, @Query(APP_ID) appId: String?): Call<OpenWeatherCurrentData?>?

    // "https://api.openweathermap.org/data/2.5/weather?q=London&type=like&APPID=someApiKey",
    @GET("weather")
    fun findWeatherByQuery(@Query(value = "q", encoded = true) query: String?,
                           @Query(OpenWeatherApiRequestBuilder.TYPE) type: String?, @Query(APP_ID) appId: String?): Call<OpenWeatherCurrentData?>?
}
