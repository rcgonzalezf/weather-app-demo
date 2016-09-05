package org.rcgonzalezf.weather.openweather.network;

import android.support.annotation.VisibleForTesting;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.converter.OpenWeatherApiModelConverter;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;

public class OpenWeatherApiRequest
    implements ApiRequest<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> {

  private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
  private static final String FORECAST = "forecast";
  private static final String URL_FORMAT = "%1$s%2$s?%3$s&APPID=%4$s";
  private final String mApiKey;
  private final ModelConverter<OpenWeatherForecastData> mModelConverter;
  private OpenWeatherApiRequestParameters mRequestParameters;

  public OpenWeatherApiRequest(String apiKey) {
    this(apiKey, new OpenWeatherApiModelConverter());
  }

  @VisibleForTesting OpenWeatherApiRequest(String apiKey,
      ModelConverter<OpenWeatherForecastData> modelConverter) {
    mApiKey = apiKey;
    mModelConverter = modelConverter;
  }

  @Override public String getBaseUrl() {
    return BASE_URL;
  }

  @Override public String getMethodName() {
    return FORECAST;
  }

  @Override public void execute(OpenWeatherApiCallback apiCallback) {
    OpenWeatherExecutor mOpenWeatherExecutor =
        new OpenWeatherExecutor(apiCallback, getExecutor(), mApiKey);
    mOpenWeatherExecutor.setModelConverter(mModelConverter);
    mOpenWeatherExecutor.performRetrofitCall(mRequestParameters);
  }

  @Override public void addRequestParameters(OpenWeatherApiRequestParameters requestParameters) {
    mRequestParameters = requestParameters;
  }

  @VisibleForTesting String url() {
    return String.format(URL_FORMAT, getBaseUrl(), getMethodName(),
        mRequestParameters.getQueryString(), mApiKey);
  }

  @VisibleForTesting Executor getExecutor() {
    return Executors.newSingleThreadExecutor();
  }
}
