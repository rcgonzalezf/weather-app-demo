package org.rcgonzalezf.weather.common;

import org.rcgonzalezf.weather.OpenWeatherApiRepository;

public class ServiceConfig {
  private static ServiceConfig sInstance = new ServiceConfig();
  private String mApiKey;
  private WeatherProvider mWeatherProvider;

  ServiceConfig() {
  }

  public static ServiceConfig getInstance() {
    return sInstance;
  }

  public static void setInstance(ServiceConfig instance) {
    sInstance = instance;
  }

  public ServiceConfig setApiKey(String apiKey) {
    mApiKey = apiKey;
    return this;
  }

  public ServiceConfig setWeatherProvider(WeatherProvider weatherProvider) {
    mWeatherProvider = weatherProvider;
    return this;
  }

  public WeatherRepository getWeatherRepository() {
    if (mApiKey == null || mWeatherProvider == null) {
      throw new IllegalStateException("Should provide an apiKey and weatherProvider");
    }

    WeatherRepository repository = null;
    switch (mWeatherProvider) {
      case OpenWeather:
        repository = new OpenWeatherApiRepository(this);
        break;
    }
    return repository;
  }

  public String getApiKey() {
    return mApiKey;
  }

  public WeatherProvider getWeatherProvider() {
    return mWeatherProvider;
  }
}
