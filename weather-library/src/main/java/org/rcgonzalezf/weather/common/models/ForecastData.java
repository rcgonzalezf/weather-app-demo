package org.rcgonzalezf.weather.common.models;

import org.rcgonzalezf.weather.openweather.models.City;

public class ForecastData {

  private City mCity;
  private double mSpeed;
  private double mDeg;
  private double mTemp;
  private long mHumidity;
  private long mSunrise;
  private long mSunset;

  public ForecastData(
      City city,
      double speed, double deg, double temp, long humidity,
      long sunrise, long sunset) {
    setCity(city);
    setSpeed(speed);
    setDeg(deg);
    setTemp(temp);
    setHumidity(humidity);
    setSunrise(sunrise);
    setSunset(sunset);
  }

  @Override public String toString() {
    return "ForecastData [name=" + getCity().getName() + ", speed=" + getSpeed() + ", deg=" + getDeg() + ", temp=" + getTemp()
        + ", humidity=" + getHumidity() + ", sunrise=" + getSunrise() + ", sunset=" + getSunset()
        + "]";
  }

  public double getSpeed() {
    return mSpeed;
  }

  public void setSpeed(double speed) {
    mSpeed = speed;
  }

  public double getDeg() {
    return mDeg;
  }

  public void setDeg(double deg) {
    mDeg = deg;
  }

  public double getTemp() {
    return mTemp;
  }

  public void setTemp(double temp) {
    mTemp = temp;
  }

  public long getHumidity() {
    return mHumidity;
  }

  public void setHumidity(long humidity) {
    mHumidity = humidity;
  }

  public long getSunrise() {
    return mSunrise;
  }

  public void setSunrise(long sunrise) {
    mSunrise = sunrise;
  }

  public long getSunset() {
    return mSunset;
  }

  public void setSunset(long sunset) {
    mSunset = sunset;
  }

  public City getCity() {
    return mCity;
  }

  public void setCity(City mCity) {
    this.mCity = mCity;
  }
}
