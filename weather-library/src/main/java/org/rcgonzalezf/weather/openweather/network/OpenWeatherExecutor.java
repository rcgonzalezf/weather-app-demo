package org.rcgonzalezf.weather.openweather.network;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.api.OpenWeatherApiService;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static org.rcgonzalezf.weather.openweather.api.OpenWeatherApiService.BASE_URL;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LIKE;

class OpenWeatherExecutor {

  private final Executor mExecutor;
  private String mApiKey;
  private ApiCallback<OpenWeatherApiResponse<ForecastData>, OpenWeatherApiError> mApiCallback;
  private static final String TAG = OpenWeatherExecutor.class.getSimpleName();
  private ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> mConverter;
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

        OpenWeatherApiService service =
            new Retrofit.Builder().baseUrl(BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApiService.class);

        Call<OpenWeatherForecastData> forecastCall;
        Call<OpenWeatherCurrentData> weatherCall;

        if (mRequestParameters.getCityName() != null) {
          forecastCall = service.findForecastFiveDaysByQuery(mRequestParameters.getCityName(), LIKE, mApiKey);
          weatherCall = service.findWeatherByQuery(mRequestParameters.getCityName(), LIKE, mApiKey);
        } else {
          forecastCall =
              service.findForecastFiveDaysByLatLon(mRequestParameters.getLat(), mRequestParameters.getLon(),
                  mApiKey);
          weatherCall =
              service.findWeatherByLatLon(mRequestParameters.getLat(), mRequestParameters.getLon(),
                  mApiKey);
        }

        try {
          OpenWeatherForecastData forecastData = forecastCall.execute().body();

          OpenWeatherCurrentData weatherData = weatherCall.execute().body();
          convertToModel(weatherData, forecastData);
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

  private void convertToModel(OpenWeatherCurrentData openWeatherCurrentData, OpenWeatherForecastData openWeatherForecastData) throws IOException {
    mConverter.fromWeatherPojo(openWeatherCurrentData);
    List<ForecastData> forecastData = mConverter.getWeatherModel();

    convertToModel(openWeatherForecastData, forecastData);
  }

  private void convertToModel(OpenWeatherForecastData openWeatherForecastData, List<ForecastData>  forecastDataFromWeather) throws IOException {

    mConverter.fromForecastPojo(openWeatherForecastData);
    List<ForecastData> forecastData = mConverter.getForecastModel();

    if (forecastData != null && !forecastData.isEmpty()) {
      forecastDataFromWeather.addAll(forecastData);

      final OpenWeatherApiResponse<ForecastData> response = new OpenWeatherApiResponse<>();
      response.setData(forecastDataFromWeather);
      mApiCallback.onSuccess(response);
    } else {
      notifyOnError();
    }
  }

  void setModelConverter(ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> converter) {
    mConverter = converter;
  }
}
