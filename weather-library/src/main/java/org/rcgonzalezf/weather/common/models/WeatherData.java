package org.rcgonzalezf.weather.common.models;

public class WeatherData {

  private double mSpeed;
  private double mDeg;
  private double mTemp;
  private long mHumidity;
  private String mDateTime;
  private int mWeatherId;
  private String mDescription;

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

  public String getDateTime() {
    return mDateTime;
  }

  public void setDateTime(String dateTime) {
    this.mDateTime = dateTime;
  }

  public int getWeatherId() {
    return mWeatherId;
  }

  public void setWeatherId(int weatherId) {
    mWeatherId = weatherId;
  }

  public String getDescription() {
    return mDescription;
  }

  public void setDescription(String description) {
    mDescription = description;
  }
}
