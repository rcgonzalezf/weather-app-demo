package org.rcgonzalezf.weather.openweather.network;

import java.util.List;
import org.rcgonzalezf.weather.common.models.converter.Data;
import org.rcgonzalezf.weather.common.network.ApiResponse;

public class OpenWeatherApiResponse implements ApiResponse {

  private List<Data> mForecastData;

  @Override public List<Data> getData() {
    return mForecastData;
  }

  public void setData(List<Data> data) {
    mForecastData = data;
  }
}
