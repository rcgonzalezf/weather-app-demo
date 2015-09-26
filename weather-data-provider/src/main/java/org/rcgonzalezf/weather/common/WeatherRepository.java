package org.rcgonzalezf.weather.common;

import org.rcgonzalezf.weather.network.WeatherApiRequestBuilder;

public interface WeatherRepository {
  void findWeather(WeatherApiRequestBuilder mApiBuilder);
}
