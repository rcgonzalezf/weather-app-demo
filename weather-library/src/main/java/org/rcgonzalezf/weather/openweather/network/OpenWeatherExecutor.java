package org.rcgonzalezf.weather.openweather.network;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.api.ForecastService;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LIKE;

class OpenWeatherExecutor {

  private final Executor mExecutor;
  private String mApiKey;
  private ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> mApiCallback;
  private static final String TAG = OpenWeatherExecutor.class.getSimpleName();
  private ModelConverter<OpenWeatherForecastData> mConverter;
  @VisibleForTesting
  WeatherLibApp mWeatherLibApp = WeatherLibApp.getInstance();

  OpenWeatherExecutor(OpenWeatherApiCallback apiCallback, Executor executor, String apiKey) {
    mApiCallback = apiCallback;
    mExecutor = executor;
    mApiKey = apiKey;
  }

  void performRetrofitCall(final OpenWeatherApiRequestParameters mRequestParameters) {

    mExecutor.execute(new Runnable() {
      @Override public void run() {
        OkHttpClient okClient = mWeatherLibApp.createOkHttpClient();

        ForecastService service =
            new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ForecastService.class);

        Call<OpenWeatherForecastData> forecastCall;
        if (mRequestParameters.getCityName() != null) {
          forecastCall = service.findByQuery(mRequestParameters.getCityName(), LIKE, mApiKey);
        } else {
          forecastCall =
              service.findByLatLon(mRequestParameters.getLat(), mRequestParameters.getLon(),
                  mApiKey);
        }

        try {
          OpenWeatherForecastData data = forecastCall.execute().body();
          convertToModel(data);
        } catch (IOException e) {
          Log.e(TAG, "IOException while getting weather", e);
          notifyOnError();
        }
      }
    });
  }

  private void notifyOnError() {
    final OpenWeatherApiError error = new OpenWeatherApiError();
    error.setMessage(mWeatherLibApp.getString(R.string.empty_result));
    error.setCode(ErrorCode.EMPTY);
    mApiCallback.onError(error);
  }

  private void convertToModel(OpenWeatherForecastData openWeatherForecastData) throws IOException {

    mConverter.fromPojo(openWeatherForecastData);
    List<ForecastData> forecastData = mConverter.getModel();

    if (forecastData != null && !forecastData.isEmpty()) {
      final OpenWeatherApiResponse response = new OpenWeatherApiResponse();
      response.setData(forecastData);
      mApiCallback.onSuccess(response);
    } else {
      notifyOnError();
    }
  }

  void setModelConverter(ModelConverter<OpenWeatherForecastData> converter) {
    mConverter = converter;
  }
}
