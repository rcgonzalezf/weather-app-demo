package org.rcgonzalezf.weather.openweather;

import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.List;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.Forecast;
import org.rcgonzalezf.weather.common.models.ForecastMapper;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiError;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiResponse;

public class OpenWeatherApiCallback
    implements ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> {

  private static final String TAG = OpenWeatherApiCallback.class.getSimpleName();

  private WeakReference<OnUpdateWeatherListListener> mOnUpdateWeatherListListenerWeakReference;

  public OpenWeatherApiCallback(OnUpdateWeatherListListener onUpdateWeatherListListener) {
    this.mOnUpdateWeatherListListenerWeakReference =
        new WeakReference<>(onUpdateWeatherListListener);
  }

  @Override public void onSuccess(OpenWeatherApiResponse apiResponse) {
    OnUpdateWeatherListListener onUpdateWeatherListListener =
        mOnUpdateWeatherListListenerWeakReference.get();
    if (onUpdateWeatherListListener != null) {
      final List<Forecast> forecastList =
          new ForecastMapper().withData(apiResponse.getData()).map();
      onUpdateWeatherListListener.updateList(forecastList);
    }
  }

  @Override public void onError(OpenWeatherApiError apiError) {
    Log.e(TAG, apiError.getError());
    OnUpdateWeatherListListener onUpdateWeatherListListener =
        mOnUpdateWeatherListListenerWeakReference.get();
    if (onUpdateWeatherListListener != null) {
      onUpdateWeatherListListener.onError(apiError.getError());
    }
  }
}