package org.rcgonzalezf.weather.openweather.models;

public class Main {

  public final static String temp_JSON = "temp";
  public final static String tempMin_JSON = "tempMin";
  public final static String tempMax_JSON = "tempMax";
  public final static String pressure_JSON = "pressure";
  public final static String seaLevel_JSON = "seaLevel";
  public final static String grndLevel_JSON = "grndLevel";
  public final static String humidity_JSON = "humidity";

  private double mTemp;
  private double mTempMin;
  private double mTempMax;
  private double mPressure;
  private double mSeaLevel;
  private double mGrndLevel;
  private long mHumidity;

  public double getTemp() {
    return mTemp;
  }

  public void setTemp(double temp) {
    mTemp = temp;
  }

  public double getTempMin() {
    return mTempMin;
  }

  public void setTempMin(double tempMin) {
    mTempMin = tempMin;
  }

  public double getTempMax() {
    return mTempMax;
  }

  public void setTempMax(double tempMax) {
    mTempMax = tempMax;
  }

  public double getPressure() {
    return mPressure;
  }

  public void setPressure(double pressure) {
    mPressure = pressure;
  }

  public double getSeaLevel() {
    return mSeaLevel;
  }

  public void setSeaLevel(double seaLevel) {
    mSeaLevel = seaLevel;
  }

  public double getGrndLevel() {
    return mGrndLevel;
  }

  public void setGrndLevel(double grndLevel) {
    mGrndLevel = grndLevel;
  }

  public long getHumidity() {
    return mHumidity;
  }

  public void setHumidity(long humidity) {
    mHumidity = humidity;
  }
}
