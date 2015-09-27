package org.rcgonzalezf.weather.openweather;

import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters;

public class OpenWeatherApiRepository implements WeatherRepository<OpenWeatherApiParameters> {

  private final ServiceConfig mServiceConfig;

  public OpenWeatherApiRepository(ServiceConfig serviceConfig) {
    mServiceConfig = serviceConfig;
  }

  @Override public void findWeather(OpenWeatherApiParameters requestParameters) {
  }
}
