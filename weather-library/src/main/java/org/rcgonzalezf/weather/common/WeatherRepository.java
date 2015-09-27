package org.rcgonzalezf.weather.common;

import java.util.List;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.common.network.RequestParameters;

public interface WeatherRepository<T extends RequestParameters> {
  List<WeatherData> findWeather(T requestParameters);
}
