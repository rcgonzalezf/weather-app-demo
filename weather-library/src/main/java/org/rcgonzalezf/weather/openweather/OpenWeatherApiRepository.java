package org.rcgonzalezf.weather.openweather;

import android.support.annotation.NonNull;
import java.util.List;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequest;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;

public class OpenWeatherApiRepository
    implements WeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> {

  private final ServiceConfig mServiceConfig;

  public OpenWeatherApiRepository(ServiceConfig serviceConfig) {
    mServiceConfig = serviceConfig;
  }

  @Override
  public List<ForecastData> findWeather(OpenWeatherApiRequestParameters requestParameters) {
    throw new UnsupportedOperationException("This repository doesn't perform sync calls");
  }

  @Override public void findWeather(OpenWeatherApiRequestParameters requestParameters,
      OpenWeatherApiCallback apiCallback) {
    ApiRequest<OpenWeatherApiRequestParameters> request = getApiRequest();
    request.addRequestParameters(requestParameters);
    request.execute(apiCallback);
  }

  @Override public ApiRequest<OpenWeatherApiRequestParameters> getApiRequest() {
    return getOpenWeatherApiRequest();
  }

  @NonNull private OpenWeatherApiRequest getOpenWeatherApiRequest() {
    return new OpenWeatherApiRequest(mServiceConfig.getApiKey());
  }
}
