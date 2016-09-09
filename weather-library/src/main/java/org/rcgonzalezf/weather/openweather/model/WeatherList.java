package org.rcgonzalezf.weather.openweather.model;

public class WeatherList {

  private Wind wind;

  private Weather[] weather;

  private String dt_txt;

  private Main main;

  public Wind getWind() {
    return wind;
  }

  public void setWind(Wind wind) {
    this.wind = wind;
  }

  public Weather[] getWeather() {
    return weather;
  }

  public void setWeather(Weather[] weather) {
    this.weather = weather;
  }

  public String getDt_txt() {
    return dt_txt;
  }

  public void setDt_txt(String dt_txt) {
    this.dt_txt = dt_txt;
  }

  public Main getMain() {
    return main;
  }

  public void setMain(Main main) {
    this.main = main;
  }
}
