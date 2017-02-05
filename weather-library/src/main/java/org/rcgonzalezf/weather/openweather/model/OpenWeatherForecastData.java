package org.rcgonzalezf.weather.openweather.model;

import com.google.gson.annotations.SerializedName;
import org.rcgonzalezf.weather.openweather.converter.Data;
// Gson
@SuppressWarnings("unused")
public class OpenWeatherForecastData implements Data {

  private String cnt;

  private String cod;

  @SerializedName("list") private java.util.List<WeatherList> weatherList;

  private City city;

  public String getCnt() {
    return cnt;
  }

  public String getCod() {
    return cod;
  }

  public java.util.List<WeatherList> getWeatherList() {
    return weatherList;
  }

  public City getCity() {
    return city;
  }
}
