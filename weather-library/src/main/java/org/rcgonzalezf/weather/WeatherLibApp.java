package org.rcgonzalezf.weather;

import android.app.Application;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherProvider;

public class WeatherLibApp extends Application {

  private ServiceConfig mServiceConfig;
  private static WeatherLibApp sAppInstance = null;

  @Override public void onCreate() {
    super.onCreate();
    mServiceConfig = ServiceConfig.getInstance();
    mServiceConfig.setApiKey(getString(R.string.open_weather_map_api_key));
    mServiceConfig.setWeatherProvider(WeatherProvider.OpenWeather);
  }

  public static WeatherLibApp getInstance() {
    return sAppInstance;
  }

  protected static void setAppInstance(WeatherLibApp appInstance) {
    sAppInstance = appInstance;
  }


}
