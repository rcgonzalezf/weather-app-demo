package org.rcgonzalezf.weather.common.models;

public class ForecastData {

  public String mName;
  public double mSpeed;
  public double mDeg;
  public double mTemp;
  public long mHumidity;
  public long mSunrise;
  public long mSunset;

  public ForecastData(String name, double speed, double deg, double temp, long humidity,
      long sunrise, long sunset) {
    mName = name;
    mSpeed = speed;
    mDeg = deg;
    mTemp = temp;
    mHumidity = humidity;
    mSunrise = sunrise;
    mSunset = sunset;
  }

  @Override public String toString() {
    return "ForecastData [name=" + mName + ", speed=" + mSpeed + ", deg=" + mDeg + ", temp=" + mTemp
        + ", humidity=" + mHumidity + ", sunrise=" + mSunrise + ", sunset=" + mSunset + "]";
  }
}
