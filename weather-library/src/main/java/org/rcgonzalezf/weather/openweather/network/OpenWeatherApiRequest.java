package org.rcgonzalezf.weather.openweather.network;

import androidx.annotation.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.converter.OpenWeatherApiModelConverter;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;

public class OpenWeatherApiRequest
    implements ApiRequest<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> {

  private final String mApiKey;
  private final ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> mModelConverter;
  private OpenWeatherApiRequestParameters mRequestParameters;

  public OpenWeatherApiRequest(String apiKey) {
    this(apiKey, new OpenWeatherApiModelConverter());
  }

  @VisibleForTesting
  OpenWeatherApiRequest(String apiKey,
                        ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> modelConverter) {
    mApiKey = apiKey;
    mModelConverter = modelConverter;
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

  @VisibleForTesting Executor getExecutor() {
    return Executors.newSingleThreadExecutor();
  }
}
