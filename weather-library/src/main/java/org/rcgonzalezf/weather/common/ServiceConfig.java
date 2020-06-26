package org.rcgonzalezf.weather.common;

import androidx.annotation.NonNull;

import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.common.network.ApiError;
import org.rcgonzalezf.weather.common.network.ApiResponse;
import org.rcgonzalezf.weather.common.network.RequestParameters;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiRepository;

public class ServiceConfig {
  private static ServiceConfig sInstance = new ServiceConfig();
  private String mApiKey;
  private WeatherProvider mWeatherProvider;

  ServiceConfig() {
  }

  public static ServiceConfig getInstance() {
    return sInstance;
  }

  static void setInstance(ServiceConfig instance) {
    sInstance = instance;
  }

  public ServiceConfig setApiKey(@NonNull String apiKey) {
    mApiKey = apiKey;
    return this;
  }

  public ServiceConfig setWeatherProvider(@NonNull WeatherProvider weatherProvider) {
    mWeatherProvider = weatherProvider;
    return this;
  }

  public <T extends RequestParameters, A extends ApiCallback<? extends ApiResponse, ? extends ApiError>> WeatherRepository<T, A> getWeatherRepository() {
    //noinspection ConstantConditions The NonNull annotations are just a lint warning for devs not a guarantee that this can't be null
    if (getApiKey() == null || getWeatherProvider() == null) {
      throw new IllegalStateException("Should provide an apiKey and weatherProvider");
    }

    WeatherRepository repository = null;
    switch (getWeatherProvider()) {
      case OpenWeather:
        repository = new OpenWeatherApiRepository(this);
        break;
    }
    return repository;
  }

  @NonNull
  public String getApiKey() {
    return mApiKey;
  }

  @NonNull
  private WeatherProvider getWeatherProvider() {
    return mWeatherProvider;
  }
}
