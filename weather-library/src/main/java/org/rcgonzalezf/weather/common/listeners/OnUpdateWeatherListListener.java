package org.rcgonzalezf.weather.common.listeners;

import java.util.List;
import org.rcgonzalezf.weather.common.models.Forecast;

public interface OnUpdateWeatherListListener {
  void updateList(List<Forecast> forecastList);
}
