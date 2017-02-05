package org.rcgonzalezf.weather.common.listeners;

import android.support.annotation.NonNull;
import java.util.List;
import org.rcgonzalezf.weather.common.models.Forecast;

public interface OnUpdateWeatherListListener {
  void updateList(@NonNull List<Forecast> forecastList);

  void onError(String error);
}
