package org.rcgonzalezf.weather.openweather.models;

public class Sys {

  public final static String message_JSON = "message";
  public final static String country_JSON = "country";
  public final static String sunrise_JSON = "sunrise";
  public final static String sunset_JSON = "sunset";

  private double mMessage;
  private String mCountry;
  private long mSunrise;
  private long mSunset;

  public double getMessage() {
    return mMessage;
  }

  public void setMessage(double message) {
    mMessage = message;
  }

  public String getCountry() {
    return mCountry;
  }

  public void setCountry(String country) {
    mCountry = country;
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
}
