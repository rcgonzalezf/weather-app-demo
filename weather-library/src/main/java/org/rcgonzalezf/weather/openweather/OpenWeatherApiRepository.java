package org.rcgonzalezf.weather.openweather;

import java.util.List;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;

public class OpenWeatherApiRepository
    implements WeatherRepository<OpenWeatherApiRequestParameters> {

  private final ServiceConfig mServiceConfig;

  public OpenWeatherApiRepository(ServiceConfig serviceConfig) {
    mServiceConfig = serviceConfig;
  }

  @Override
  public List<WeatherData> findWeather(OpenWeatherApiRequestParameters requestParameters) {
    return null;
  }
}
