package org.rcgonzalezf.weather.openweather.api;

import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LAT;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LON;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.TYPE;

public interface ForecastService {

  String APP_ID = "APPID";

  // "http://api.openweathermap.org/data/2.5/forecast?lon=139.0&lat=35.0&APPID=someApiKey",
  @GET("forecast") Call<OpenWeatherForecastData> findByLatLon(@Query(LON) String lon,
      @Query(LAT) String lat, @Query(APP_ID) String appId);

  // "http://api.openweathermap.org/data/2.5/forecast?q=London&type=like&APPID=someApiKey",
  @GET("forecast") Call<OpenWeatherForecastData> findByQuery(@Query("q") String query,
      @Query(TYPE) String type, @Query(APP_ID) String appId);
}
