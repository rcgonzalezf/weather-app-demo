package org.rcgonzalezf.weather.openweather.model;

import com.google.gson.annotations.SerializedName;
import org.rcgonzalezf.weather.openweather.converter.Data;

public class OpenWeatherForecastData implements Data {
  private String message;

  private String cnt;

  private String cod;

  @SerializedName("list")
  private java.util.List<WeatherList> weatherList;

  private City city;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCnt() {
    return cnt;
  }

  public void setCnt(String cnt) {
    this.cnt = cnt;
  }

  public String getCod() {
    return cod;
  }

  public void setCod(String cod) {
    this.cod = cod;
  }

  public java.util.List<WeatherList> getWeatherList() {
    return weatherList;
  }

  public void setWeatherList(java.util.List<WeatherList> weatherList) {
    this.weatherList = weatherList;
  }

  public City getCity() {
    return city;
  }

  public void setCity(City city) {
    this.city = city;
  }
}
