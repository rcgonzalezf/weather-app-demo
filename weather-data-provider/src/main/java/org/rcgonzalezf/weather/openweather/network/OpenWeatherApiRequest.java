package org.rcgonzalezf.weather.openweather.network;

import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.common.network.ApiRequest;

public class OpenWeatherApiRequest implements ApiRequest<OpenWeatherApiRequestParameters> {

  private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
  private static final String FORECAST = "forecast";
  private static final String URL_FORMAT = "%1$s%2$s?%3$s&APPID=%4$s";
  private final String mApiKey;
  private OpenWeatherApiRequestParameters mRequestParameters;

  public OpenWeatherApiRequest(String apiKey) {
    mApiKey = apiKey;
  }

  @Override public String getBaseUrl() {
    return BASE_URL;
  }

  @Override public String getMethodName() {
    return FORECAST;
  }

  @Override public void execute(ApiCallback apiCallback) {

  }

  @Override public void addRequestParameters(OpenWeatherApiRequestParameters requestParameters) {
    mRequestParameters = requestParameters;
  }

  protected String url() {
    return String.format(URL_FORMAT,
        BASE_URL,
        FORECAST,
        mRequestParameters.getQueryString(),
        mApiKey);
  }
}
