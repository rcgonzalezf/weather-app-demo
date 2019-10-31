package org.rcgonzalezf.weather.openweather.api;

import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LAT;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LON;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.TYPE;

public interface OpenWeatherApiService {

  String APP_ID = "APPID";
  String BASE_URL = "https://api.openweathermap.org/data/2.5/";

  /*
  Forecast for 5 days, more info at:
  https://openweathermap.org/forecast5
   */

  // "https://api.openweathermap.org/data/2.5/forecast?lon=139.0&lat=35.0&APPID=someApiKey",
  @GET("forecast") Call<OpenWeatherForecastData> findForecastFiveDaysByLatLon(@Query(LAT) String lat,
      @Query(LON) String lon, @Query(APP_ID) String appId);

  // "https://api.openweathermap.org/data/2.5/forecast?q=London&type=like&APPID=someApiKey",
  @GET("forecast") Call<OpenWeatherForecastData> findForecastFiveDaysByQuery(@Query(value = "q", encoded = true) String query,
      @Query(TYPE) String type, @Query(APP_ID) String appId);

  /*
  Current weather more info at:
  https://openweathermap.org/current
   */

  // "https://api.openweathermap.org/data/2.5/weather?lon=139.0&lat=35.0&APPID=someApiKey",
  @GET("weather") Call<OpenWeatherCurrentData> findWeatherByLatLon(@Query(LAT) String lat,
      @Query(LON) String lon, @Query(APP_ID) String appId);

  // "https://api.openweathermap.org/data/2.5/weather?q=London&type=like&APPID=someApiKey",
  @GET("weather") Call<OpenWeatherCurrentData> findWeatherByQuery(@Query(value = "q", encoded = true) String query,
      @Query(TYPE) String type, @Query(APP_ID) String appId);
}
