package org.rcgonzalezf.weather.openweather;

import java.lang.ref.WeakReference;
import java.util.List;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.Forecast;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiError;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiResponse;
import org.rcgonzalezf.weather.common.models.ForecastMapper;

public class OpenWeatherApiCallback
    implements ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> {

  private WeakReference<OnUpdateWeatherListListener> mOnUpdateWeatherListListenerWeakReference;

  public OpenWeatherApiCallback(OnUpdateWeatherListListener callerActivity) {
    this.mOnUpdateWeatherListListenerWeakReference = new WeakReference<>(callerActivity);
  }

  @Override public void onSuccess(OpenWeatherApiResponse apiResponse) {
    OnUpdateWeatherListListener onUpdateWeatherListListener = mOnUpdateWeatherListListenerWeakReference.get();
    if (onUpdateWeatherListListener != null) {
      final List<Forecast> forecastList =
          new ForecastMapper().withData(apiResponse.getData()).map();
      onUpdateWeatherListListener.updateList(forecastList);
    }
  }

  @Override public void onError(OpenWeatherApiError apiError) {
    apiError.getError();
  }
}