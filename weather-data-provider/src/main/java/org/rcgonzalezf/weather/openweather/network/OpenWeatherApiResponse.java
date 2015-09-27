package org.rcgonzalezf.weather.openweather.network;

import java.util.List;
import org.rcgonzalezf.weather.WeatherData;
import org.rcgonzalezf.weather.common.network.ApiResponse;

public class OpenWeatherApiResponse implements ApiResponse {

  private List<WeatherData> mWeatherData;

  @Override public List<WeatherData> getData() {
    return mWeatherData;
  }
}
