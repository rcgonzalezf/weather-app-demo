package org.rcgonzalezf.weather.openweather.models;

public class Wind {

  public final static String deg_JSON = "deg";
  public final static String speed_JSON = "speed";

  private double mSpeed;
  private double mDeg;

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
}
