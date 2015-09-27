package org.rcgonzalezf.weather.common.network;

public interface ApiCallback<T extends ApiResponse, E extends ApiError> {
  void onSuccess(T apiResponse);
  void onError(E apiError);
}
