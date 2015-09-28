package rcgonzalezf.org.weather.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Forecast implements WeatherViewModel {

  public Forecast() {
  }

  private int mWeatherId;
  private int mCityId;
  private String mCityName;
  private String mSpeed;
  private String mTemp;
  private String mHumidity;
  private String mDateTime;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(mCityId);
    dest.writeString(mCityName);
    dest.writeString(mSpeed);
    dest.writeString(mTemp);
    dest.writeString(mHumidity);
    dest.writeString(mDateTime);
    dest.writeInt(mWeatherId);
  }

  public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>() {
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
    mSpeed = in.readString();
    mTemp = in.readString();
    mHumidity = in.readString();
    mDateTime = in.readString();
    mWeatherId = in.readInt();
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

  @Override public String getSpeed() {
    return mSpeed;
  }

  public void setSpeed(String speed) {
    this.mSpeed = speed;
  }

  @Override public String getTemperature() {
    return mTemp;
  }

  public void setTemperature(String temp) {
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
}
