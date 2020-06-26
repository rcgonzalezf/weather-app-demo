package org.rcgonzalezf.weather.openweather;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;
import java.util.List;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.WeatherInfo;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.WeatherInfoMapper;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiError;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiResponse;

public class OpenWeatherApiCallback
    implements ApiCallback<OpenWeatherApiResponse<ForecastData>, OpenWeatherApiError> {

  private static final String TAG = OpenWeatherApiCallback.class.getSimpleName();

  private WeakReference<OnUpdateWeatherListListener> mOnUpdateWeatherListListenerWeakReference;

  public OpenWeatherApiCallback(OnUpdateWeatherListListener onUpdateWeatherListListener) {
    this.mOnUpdateWeatherListListenerWeakReference =
        new WeakReference<>(onUpdateWeatherListListener);
  }

  @Override public void onSuccess(OpenWeatherApiResponse<ForecastData> apiResponse) {
    OnUpdateWeatherListListener onUpdateWeatherListListener =
        mOnUpdateWeatherListListenerWeakReference.get();
    onSuccess(apiResponse, onUpdateWeatherListListener);
  }

  @VisibleForTesting
  void onSuccess(OpenWeatherApiResponse<ForecastData> apiResponse,
      OnUpdateWeatherListListener onUpdateWeatherListListener) {
    if (onUpdateWeatherListListener != null) {
      final List<WeatherInfo> weatherInfoList =
          new WeatherInfoMapper().withData(apiResponse.getData()).map();
      onUpdateWeatherListListener.updateList(weatherInfoList);
    }
  }

  @Override public void onError(OpenWeatherApiError apiError) {
    Log.e(TAG, apiError.getError() + apiError.getCode());
    OnUpdateWeatherListListener onUpdateWeatherListListener =
        mOnUpdateWeatherListListenerWeakReference.get();
    onError(apiError, onUpdateWeatherListListener);
  }

  @VisibleForTesting
  void onError(OpenWeatherApiError apiError,
      OnUpdateWeatherListListener onUpdateWeatherListListener) {
    if (onUpdateWeatherListListener != null) {
      onUpdateWeatherListListener.onError(apiError.getError());
    }
  }
}