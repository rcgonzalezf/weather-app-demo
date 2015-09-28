package org.rcgonzalezf.weather.common;

import java.util.List;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.common.network.RequestParameters;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;

public interface WeatherRepository<T extends RequestParameters> {
  List<ForecastData> findWeather(T requestParameters);

  void findWeather(OpenWeatherApiRequestParameters requestParameters, ApiCallback apiCallback);

  ApiRequest getApiRequest();
}
