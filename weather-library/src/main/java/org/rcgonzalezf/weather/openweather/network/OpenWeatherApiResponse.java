package org.rcgonzalezf.weather.openweather.network;

import java.util.List;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.common.network.ApiResponse;

public class OpenWeatherApiResponse implements ApiResponse {

  private List<WeatherData> mWeatherData;

  @Override public List<WeatherData> getData() {
    return mWeatherData;
  }

  public void setData(List<WeatherData> data) {
    mWeatherData = data;
  }
}
