package org.rcgonzalezf.weather.openweather.model;

import org.rcgonzalezf.weather.common.models.converter.Data;

public class OpenWeatherCurrentData implements Data {

  private String name;

  private String cod;

  private Wind wind;

  private java.util.List<Weather> weather;

  private Sys sys;

  private long dt;

  private Main main;

  public Wind getWind() {
    return wind;
  }

  public java.util.List<Weather> getWeather() {
    return weather;
  }

  public long getDt() {
    return dt;
  }

  public Main getMain() {
    return main;
  }

  public String getCod() {
    return cod;
  }

  public String getName() {
    return name;
  }

  public Sys getSys() {
    return sys;
  }
}
