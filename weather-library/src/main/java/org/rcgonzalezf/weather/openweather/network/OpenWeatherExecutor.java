package org.rcgonzalezf.weather.openweather.network;

import android.util.Log;
import androidx.annotation.VisibleForTesting;
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

// TODO transforming this to Kotlin with the tool made tests to fail.
class OpenWeatherExecutor {

  private final Executor executor;
  private String apiKey;
  private ApiCallback<OpenWeatherApiResponse<ForecastData>, OpenWeatherApiError> apiCallback;
  private static final String TAG = OpenWeatherExecutor.class.getSimpleName();
  private ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> converter;
  @VisibleForTesting
  WeatherLibApp weatherLibApp = WeatherLibApp.getInstance();

  OpenWeatherExecutor(OpenWeatherApiCallback apiCallback, Executor executor, String apiKey) {
    this.apiCallback = apiCallback;
    this.executor = executor;
    this.apiKey = apiKey;
  }

  void performRetrofitCall(final OpenWeatherApiRequestParameters requestParameters) {
    executor.execute(new Runnable() {
      @Override public void run() {
        OkHttpClient okClient = weatherLibApp.createOkHttpClient();

        OpenWeatherApiService service =
            new Retrofit.Builder().baseUrl(BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApiService.class);

        Call<OpenWeatherForecastData> forecastCall;
        Call<OpenWeatherCurrentData> weatherCall;

        if (requestParameters.getCityName() != null) {
          forecastCall = service.findForecastFiveDaysByQuery(requestParameters.getCityName(), LIKE, apiKey);
          weatherCall = service.findWeatherByQuery(requestParameters.getCityName(), LIKE, apiKey);
        } else {
          forecastCall =
              service.findForecastFiveDaysByLatLon(requestParameters.getLat(), requestParameters.getLon(),
                      apiKey);
          weatherCall =
              service.findWeatherByLatLon(requestParameters.getLat(), requestParameters.getLon(),
                      apiKey);
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
    error.setMessage(weatherLibApp.getString(R.string.empty_result));
    error.setCode(ErrorCode.EMPTY);
    apiCallback.onError(error);
  }

  private void convertToModel(OpenWeatherCurrentData openWeatherCurrentData, OpenWeatherForecastData openWeatherForecastData) throws IOException {
    converter.fromWeatherPojo(openWeatherCurrentData);
    List<ForecastData> forecastData = converter.getWeatherModel();
    convertToModel(openWeatherForecastData, forecastData);
  }

  private void convertToModel(OpenWeatherForecastData openWeatherForecastData, List<ForecastData>  forecastDataFromWeather) throws IOException {
    converter.fromForecastPojo(openWeatherForecastData);
    List<ForecastData> forecastData = converter.getForecastModel();

    if (forecastData != null && !forecastData.isEmpty()) {
      forecastDataFromWeather.addAll(forecastData);

      final OpenWeatherApiResponse<ForecastData> response = new OpenWeatherApiResponse<>();
      response.setData(forecastDataFromWeather);
      apiCallback.onSuccess(response);
    } else {
      notifyOnError();
    }
  }

  void setModelConverter(ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> converter) {
    this.converter = converter;
  }
}
