package org.rcgonzalezf.weather.openweather.network;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.openweather.converter.OpenWeatherApiModelConverter;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;

public class OpenWeatherApiRequest implements ApiRequest<OpenWeatherApiRequestParameters> {

  private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
  private static final String FORECAST = "forecast";
  private static final String URL_FORMAT = "%1$s%2$s?%3$s&APPID=%4$s";
  private final String mApiKey;
  private final ModelConverter<Void, OpenWeatherApiRawData> mModelConverter;
  private OpenWeatherApiRequestParameters mRequestParameters;
  private OpenWeatherExecutor mOpenWeatherExecutor;

  public OpenWeatherApiRequest(String apiKey) {
    this(apiKey, new OpenWeatherApiModelConverter());
  }

  public OpenWeatherApiRequest(String apiKey,
      ModelConverter<Void, OpenWeatherApiRawData> modelConverter) {
    mApiKey = apiKey;
    mModelConverter = modelConverter;
  }

  @Override public String getBaseUrl() {
    return BASE_URL;
  }

  @Override public String getMethodName() {
    return FORECAST;
  }

  @Override public void execute(ApiCallback apiCallback) {
    mOpenWeatherExecutor = new OpenWeatherExecutor(apiCallback, getExecutor());
    mOpenWeatherExecutor.setModelConverter(mModelConverter);
    mOpenWeatherExecutor.performNetworkCall(url());
  }

  @Override public void addRequestParameters(OpenWeatherApiRequestParameters requestParameters) {
    mRequestParameters = requestParameters;
  }

  protected String url() {
    return String.format(URL_FORMAT, getBaseUrl(), getMethodName(),
        mRequestParameters.getQueryString(), mApiKey);
  }

  protected Executor getExecutor() {
    return Executors.newSingleThreadExecutor();
  }
}
