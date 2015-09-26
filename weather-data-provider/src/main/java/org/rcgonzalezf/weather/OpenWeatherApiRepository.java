package org.rcgonzalezf.weather;

import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.network.WeatherApiRequestBuilder;

public class OpenWeatherApiRepository  implements WeatherRepository {

  private static WeatherRepository instance;

  public static WeatherRepository getInstance() {
    return instance;
  }

  @Override
  public void findWeather(WeatherApiRequestBuilder mApiBuilder) {

  }
}
