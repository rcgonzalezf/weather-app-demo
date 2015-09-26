package org.rcgonzalezf.weather.network;

public class WeatherApiRequestBuilder {

  private String mCityId;

  public WeatherApiRequestBuilder withCityId(String cityId) {
    mCityId = cityId;
    return this;
  }
}
