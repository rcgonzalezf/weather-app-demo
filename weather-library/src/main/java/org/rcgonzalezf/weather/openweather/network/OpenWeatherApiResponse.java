package org.rcgonzalezf.weather.openweather.network;

import java.util.List;
import org.rcgonzalezf.weather.common.models.converter.Data;
import org.rcgonzalezf.weather.common.network.ApiResponse;

public class OpenWeatherApiResponse<D extends Data> implements ApiResponse<D> {

  private List<D> mForecastData;

  @Override public List<D> getData() {
    return mForecastData;
  }

  public void setData(List<D> data) {
    mForecastData = data;
  }
}
