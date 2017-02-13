package org.rcgonzalezf.weather.common.models;

import android.os.Parcel;

public final class WeatherInfo implements WeatherViewModel {

  public WeatherInfo() {
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
    dest.writeInt(getId());
    dest.writeString(getCityName());
    dest.writeDouble(getSpeed());
    dest.writeDouble(getTemperature());
    dest.writeString(getHumidity());
    dest.writeString(getDateTime());
    dest.writeInt(getWeatherId());
    dest.writeString(getCountry());
    dest.writeDouble(getDeg());
    dest.writeString(getDescription());
  }

  public static final Creator<WeatherInfo> CREATOR = new Creator<WeatherInfo>() {
    public WeatherInfo createFromParcel(Parcel in) {
      return new WeatherInfo(in);
    }

    public WeatherInfo[] newArray(int size) {
      return new WeatherInfo[size];
    }
  };

  private WeatherInfo(Parcel in) {
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

  @Override public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;

    WeatherInfo weatherInfo = (WeatherInfo) other;

    if (mWeatherId != weatherInfo.mWeatherId) return false;
    if (mCityId != weatherInfo.mCityId) return false;
    if (Double.compare(weatherInfo.mSpeed, mSpeed) != 0) return false;
    if (Double.compare(weatherInfo.mTemp, mTemp) != 0) return false;
    if (Double.compare(weatherInfo.mDeg, mDeg) != 0) return false;
    if (!mCityName.equals(weatherInfo.mCityName)) return false;
    if (mHumidity != null ? !mHumidity.equals(weatherInfo.mHumidity) : weatherInfo.mHumidity != null) {
      return false;
    }
    if (mDateTime != null ? !mDateTime.equals(weatherInfo.mDateTime) : weatherInfo.mDateTime != null) {
      return false;
    }
    if (!mCountry.equals(weatherInfo.mCountry)) return false;
    return mDescription != null ? mDescription.equals(weatherInfo.mDescription)
        : weatherInfo.mDescription == null;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = mWeatherId;
    result = 31 * result + mCityId;
    result = 31 * result + mCityName.hashCode();
    temp = Double.doubleToLongBits(mSpeed);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(mTemp);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (mHumidity != null ? mHumidity.hashCode() : 0);
    result = 31 * result + (mDateTime != null ? mDateTime.hashCode() : 0);
    result = 31 * result + mCountry.hashCode();
    temp = Double.doubleToLongBits(mDeg);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
    return result;
  }
}
