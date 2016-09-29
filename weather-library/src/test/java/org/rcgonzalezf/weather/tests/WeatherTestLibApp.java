package org.rcgonzalezf.weather.tests;

import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherProvider;

public class WeatherTestLibApp extends WeatherLibApp {

  private ServiceConfig mServiceConfig;

  @Override public void onCreate() {
    super.onCreate();
    setAppInstance(this);
    mServiceConfig = ServiceConfig.getInstance();
    mServiceConfig.setApiKey(getString(R.string.open_weather_map_api_key));
    mServiceConfig.setWeatherProvider(WeatherProvider.OpenWeather);
  }

  @Override public OkHttpClient createOkHttpClient() {
    return new OkHttpClient.Builder().build();
  }
}
