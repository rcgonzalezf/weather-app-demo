package org.rcgonzalezf.weather.common;

import java.util.List;
import org.rcgonzalezf.weather.common.models.converter.Data;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.common.network.ApiError;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.common.network.ApiResponse;
import org.rcgonzalezf.weather.common.network.RequestParameters;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;

public interface WeatherRepository<T extends RequestParameters, A extends ApiCallback<? extends ApiResponse, ? extends ApiError>> {
  List<Data> findWeather(T requestParameters);

  void findWeather(OpenWeatherApiRequestParameters requestParameters, A apiCallback);

  ApiRequest getApiRequest();
}
