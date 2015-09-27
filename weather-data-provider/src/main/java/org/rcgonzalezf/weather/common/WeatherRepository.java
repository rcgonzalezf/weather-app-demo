package org.rcgonzalezf.weather.common;

import org.rcgonzalezf.weather.common.network.RequestParameters;

public interface WeatherRepository<T extends RequestParameters> {
  void findWeather(T requestParameters);
}
