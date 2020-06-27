package org.rcgonzalezf.weather.common.listeners;

import androidx.annotation.NonNull;
import java.util.List;
import org.rcgonzalezf.weather.common.models.WeatherInfo;

public interface OnUpdateWeatherListListener {
  void updateList(@NonNull List<WeatherInfo> weatherInfoList);

  void onError(String error);
}
