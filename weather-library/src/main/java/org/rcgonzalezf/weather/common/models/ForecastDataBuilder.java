package org.rcgonzalezf.weather.common.models;

import org.rcgonzalezf.weather.openweather.models.City;

public class ForecastDataBuilder {
  private City city;
  private int count;

  public ForecastDataBuilder setCity(City city) {
    this.city = city;
    return this;
  }

  public ForecastDataBuilder setCount(int count) {
    this.count = count;
    return this;
  }

  public ForecastData createForecastData() {
    return new ForecastData(city, count);
  }
}