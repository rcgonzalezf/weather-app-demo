package org.rcgonzalezf.weather.common.network;

import org.rcgonzalezf.weather.openweather.network.ErrorCode;

public interface ApiError<T> {
  T getError();

  void setCode(ErrorCode code);
}
