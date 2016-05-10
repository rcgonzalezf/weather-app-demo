package org.rcgonzalezf.weather.openweather.network;

import android.util.Log;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.api.ForecastService;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LIKE;

public class OpenWeatherExecutor {

  private final Executor mExecutor;
  private String mApiKey;
  private ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> mApiCallback;
  private static final String TAG = OpenWeatherExecutor.class.getSimpleName();
  private ModelConverter mConverter;

  public OpenWeatherExecutor(ApiCallback apiCallback, Executor executor, String apiKey) {
    mApiCallback = apiCallback;
    mExecutor = executor;
    mApiKey = apiKey;
  }

  public void performRetrofitCall(final OpenWeatherApiRequestParameters mRequestParameters) {

    mExecutor.execute(new Runnable() {
      @Override public void run() {
        OkHttpClient okClient =
            new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        ForecastService service =
            new Retrofit.Builder().baseUrl("http://api.openweathermap.org/data/2.5/")
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ForecastService.class);

        Call<OpenWeatherForecastData> forecastCall = null;
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
    error.setMessage(WeatherLibApp.getInstance().getString(R.string.empty_result));
    error.setCode(ErrorCode.EMPTY);
    mApiCallback.onError(error);
  }

  private void convertToModel(OpenWeatherForecastData openWeatherForecastData) throws IOException {
    //noinspection unchecked
    mConverter.fromPojo(openWeatherForecastData);
    //noinspection unchecked
    List<ForecastData> forecastData = mConverter.getModel();

    if (forecastData != null && !forecastData.isEmpty()) {
      final OpenWeatherApiResponse response = new OpenWeatherApiResponse();
      response.setData(forecastData);
      mApiCallback.onSuccess(response);
    } else {
      notifyOnError();
    }
  }

  public void setModelConverter(
      ModelConverter<Void, OpenWeatherApiRawData, OpenWeatherForecastData> converter) {
    mConverter = converter;
  }
}
