package org.rcgonzalezf.weather.openweather.network;

import java.util.List;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.common.network.ApiResponse;

public class OpenWeatherApiResponse implements ApiResponse {

  private List<ForecastData> mForecastData;

  @Override public List<ForecastData> getData() {
    return mForecastData;
  }

  public void setData(List<ForecastData> data) {
    mForecastData = data;
  }
}
