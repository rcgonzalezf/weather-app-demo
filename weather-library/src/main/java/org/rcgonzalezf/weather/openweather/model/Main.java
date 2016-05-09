package org.rcgonzalezf.weather.openweather.model;

public class Main {
  private String temp_kf;

  private long humidity;

  private String pressure;

  private String temp_max;

  private String sea_level;

  private String temp_min;

  private double temp;

  private String grnd_level;

  public String getTemp_kf() {
    return temp_kf;
  }

  public void setTemp_kf(String temp_kf) {
    this.temp_kf = temp_kf;
  }

  public long getHumidity() {
    return humidity;
  }

  public void setHumidity(long humidity) {
    this.humidity = humidity;
  }

  public String getPressure() {
    return pressure;
  }

  public void setPressure(String pressure) {
    this.pressure = pressure;
  }

  public String getTemp_max() {
    return temp_max;
  }

  public void setTemp_max(String temp_max) {
    this.temp_max = temp_max;
  }

  public String getSea_level() {
    return sea_level;
  }

  public void setSea_level(String sea_level) {
    this.sea_level = sea_level;
  }

  public String getTemp_min() {
    return temp_min;
  }

  public void setTemp_min(String temp_min) {
    this.temp_min = temp_min;
  }

  public double getTemp() {
    return temp;
  }

  public void setTemp(double temp) {
    this.temp = temp;
  }

  public String getGrnd_level() {
    return grnd_level;
  }

  public void setGrnd_level(String grnd_level) {
    this.grnd_level = grnd_level;
  }
}
