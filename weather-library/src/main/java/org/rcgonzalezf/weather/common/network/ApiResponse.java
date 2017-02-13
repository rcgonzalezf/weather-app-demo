package org.rcgonzalezf.weather.common.network;

import java.util.List;
import org.rcgonzalezf.weather.openweather.model.ForecastData;

public interface ApiResponse {

  List<ForecastData> getData();
}
