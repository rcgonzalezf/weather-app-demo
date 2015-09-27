package org.rcgonzalezf.weather.common;

import java.util.List;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.network.RequestParameters;

public interface WeatherRepository<T extends RequestParameters> {
  List<ForecastData> findWeather(T requestParameters);
}
