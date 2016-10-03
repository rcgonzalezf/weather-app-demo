package org.rcgonzalezf.weather.openweather.model;

// Gson
@SuppressWarnings("unused") public class WeatherList {

  private Wind wind;

  private Weather[] weather;

  private String dt_txt;

  private Main main;

  public Wind getWind() {
    return wind;
  }

  public Weather[] getWeather() {
    return weather;
  }

  public String getDt_txt() {
    return dt_txt;
  }

  public Main getMain() {
    return main;
  }
}
