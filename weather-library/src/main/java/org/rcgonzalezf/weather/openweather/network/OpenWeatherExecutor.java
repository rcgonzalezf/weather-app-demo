package org.rcgonzalezf.weather.openweather.network;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;

public class OpenWeatherExecutor {

  private final Executor mExecutor;
  private ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> mApiCallback;
  private InputStream mInputStream;
  private ModelConverter<Void, OpenWeatherApiRawData> mConverter;
  private static final String TAG = OpenWeatherExecutor.class.getSimpleName();

  public OpenWeatherExecutor(ApiCallback apiCallback, Executor executor) {
    mApiCallback = apiCallback;
    mExecutor = executor;
  }

  public void performNetworkCall(final String stringUrl) {

    mExecutor.execute(new Runnable() {
      @Override public void run() {
        try {
          final URL url = new URL(stringUrl);
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          mInputStream = urlConnection.getInputStream();
          InputStream inputStream = null;

          try {
            inputStream = new BufferedInputStream(getInputStream());
            convertToModel(inputStream);
          } finally {
            urlConnection.disconnect();
            if (inputStream != null) {
              inputStream.close();
            }
          }
        } catch (IOException e) {
          Log.e(TAG, "IOException while getting weather", e);
          notifyOnError();
        }
      }
    });
  }

  private void notifyOnError() {
    final OpenWeatherApiError error = new OpenWeatherApiError();
    error.setMessage(WeatherLibApp.getInstance().getString(R.string.empty_result));
    error.setCode(ErrorCode.EMPTY);
    mApiCallback.onError(error);
  }

  private void convertToModel(InputStream inputStream) throws IOException {
    mConverter.fromInputStream(inputStream);
    List<ForecastData> forecastData = mConverter.getModel();

    if (forecastData != null && !forecastData.isEmpty()) {
      final OpenWeatherApiResponse response = new OpenWeatherApiResponse();
      response.setData(forecastData);
      mApiCallback.onSuccess(response);
    } else {
      notifyOnError();
    }
  }

  public InputStream getInputStream() {
    return mInputStream;
  }

  public void setModelConverter(ModelConverter<Void, OpenWeatherApiRawData> converter) {
    mConverter = converter;
  }
}
