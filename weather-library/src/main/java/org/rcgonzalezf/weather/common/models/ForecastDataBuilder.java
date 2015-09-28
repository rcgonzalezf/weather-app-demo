package org.rcgonzalezf.weather.common.models;

import org.rcgonzalezf.weather.openweather.models.City;

public class ForecastDataBuilder {
  private City mCity;
  private double speed;
  private double deg;
  private double temp;
  private long humidity;
  private long sunrise;
  private long sunset;

  public ForecastDataBuilder setCity(City city) {
    mCity = city;
    return this;
  }

  public ForecastDataBuilder setSpeed(double speed) {
    this.speed = speed;
    return this;
  }

  public ForecastDataBuilder setDeg(double deg) {
    this.deg = deg;
    return this;
  }

  public ForecastDataBuilder setTemp(double temp) {
    this.temp = temp;
    return this;
  }

  public ForecastDataBuilder setHumidity(long humidity) {
    this.humidity = humidity;
    return this;
  }

  public ForecastDataBuilder setSunrise(long sunrise) {
    this.sunrise = sunrise;
    return this;
  }

  public ForecastDataBuilder setSunset(long sunset) {
    this.sunset = sunset;
    return this;
  }

  public ForecastData createForecastData() {
    return new ForecastData(mCity, speed, deg, temp, humidity, sunrise, sunset);
  }
}