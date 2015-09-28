package org.rcgonzalezf.weather.openweather.models;

import java.util.List;
import org.rcgonzalezf.weather.common.models.RawWeatherData;

public class OpenWeatherApiRawData implements RawWeatherData<OpenWeatherApiRawData> {

  public OpenWeatherApiRawData() {

  }

  @Override public OpenWeatherApiRawData get() {
    return this;
  }

  public final static String cod_JSON = "cod";
  public final static String city_JSON = "city";
  public final static String cnt_JSON = "cnt";
  public final static String list_JSON = "list";

  private int mCount;
  private long mCod;
  private City mCity;
  private List<RawListItem> mRawList;


  public long getCod() {
    return mCod;
  }

  public void setCod(long cod) {
    mCod = cod;
  }

  public City getCity() {
    if (mCity == null) {
      mCity = new City();
    }
    return mCity;
  }

  public void setCity(City city) {
    mCity = city;
  }

  public int getCount() {
    return mCount;
  }

  public void setCount(int mCount) {
    this.mCount = mCount;
  }

  public List<RawListItem> getRawList() {
    return mRawList;
  }

  public void setRawList(List<RawListItem> mList) {
    this.mRawList = mList;
  }
}
