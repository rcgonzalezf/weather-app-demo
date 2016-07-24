package org.rcgonzalezf.weather.openweather.network;

import org.rcgonzalezf.weather.common.network.ApiError;

class OpenWeatherApiError implements ApiError<String>{

  private String mMessage;
  private ErrorCode mCode;

  public void setMessage(String message) {
    mMessage = message;
  }

  @Override
  public String getError() {
    return mMessage;
  }

  @Override
  public void setCode(ErrorCode code) {
    mCode = code;
  }
}
