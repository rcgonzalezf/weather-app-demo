package org.rcgonzalezf.weather.common.models;

import android.os.Parcel;

public class Forecast implements WeatherViewModel {

  public Forecast() {
  }

  private int mWeatherId;
  private int mCityId;
  private String mCityName;
  private double mSpeed;
  private double mTemp;
  private String mHumidity;
  private String mDateTime;
  private String mCountry;
  private double mDeg;
  private String mDescription;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(mCityId);
    dest.writeString(mCityName);
    dest.writeDouble(mSpeed);
    dest.writeDouble(mTemp);
    dest.writeString(mHumidity);
    dest.writeString(mDateTime);
    dest.writeInt(mWeatherId);
    dest.writeString(getCountry());
    dest.writeDouble(mDeg);
    dest.writeString(mDescription);
  }

  public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
    public Forecast createFromParcel(Parcel in) {
      return new Forecast(in);
    }

    public Forecast[] newArray(int size) {
      return new Forecast[size];
    }
  };

  private Forecast(Parcel in) {
    mCityId = in.readInt();
    mCityName = in.readString();
    mSpeed = in.readDouble();
    mTemp = in.readDouble();
    mHumidity = in.readString();
    mDateTime = in.readString();
    mWeatherId = in.readInt();
    setCountry(in.readString());
    mDeg = in.readDouble();
    mDescription = in.readString();
  }

  @Override public int getId() {
    return mCityId;
  }

  public void setCityId(int cityId) {
    this.mCityId = cityId;
  }

  @Override public String getCityName() {
    return mCityName;
  }

  public void setCityName(String cityName) {
    this.mCityName = cityName;
  }

  @Override public double getSpeed() {
    return mSpeed;
  }

  public void setSpeed(double speed) {
    this.mSpeed = speed;
  }

  @Override public double getTemperature() {
    return mTemp;
  }

  public void setTemperature(double temp) {
    this.mTemp = temp;
  }

  @Override public String getHumidity() {
    return mHumidity;
  }

  public void setHumidity(String humitidy) {
    this.mHumidity = humitidy;
  }

  @Override public String getDateTime() {
    return mDateTime;
  }

  @Override public int getWeatherId() {
    return mWeatherId;
  }

  public void setDateTime(String dateTime) {
    this.mDateTime = dateTime;
  }

  public void setWeatherId(int weatherId) {
    mWeatherId = weatherId;
  }

  @Override public String getCountry() {
    return mCountry;
  }

  @Override public double getDeg() {
    return mDeg;
  }

  @Override public String getDescription() {
    return mDescription;
  }

  public void setCountry(String mCountry) {
    this.mCountry = mCountry;
  }

  public void setDeg(double deg) {
    mDeg = deg;
  }

  public void setDescription(String description) {
    this.mDescription = description;
  }
}
