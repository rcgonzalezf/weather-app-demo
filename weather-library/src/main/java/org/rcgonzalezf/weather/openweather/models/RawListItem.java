package org.rcgonzalezf.weather.openweather.models;

import java.util.ArrayList;
import java.util.List;

public class RawListItem {

  public RawListItem() {
  }

  public final static String dt_JSON = "dt";
  public final static String main_JSON = "main";
  public final static String weather_JSON = "weather";
  public final static String wind_JSON = "wind";
  public final static String sys_JSON = "sys";
  public final static String dt_txt_JSON = "dt_txt";



  private Sys mSys;
  private String mBase;
  private Main mMain;
  private List<Weather> mWeather = new ArrayList<Weather>();
  private Wind mWind;
  private long mDt;
  private long mId;
  private String mDateTime;

  public Sys getSys() {
    if (mSys == null) {
      mSys = new Sys();
    }
    return mSys;
  }

  public void setSys(Sys sys) {
    mSys = sys;
  }

  public String getBase() {
    return mBase;
  }

  public void setBase(String base) {
    mBase = base;
  }

  public Main getMain() {
    if (mMain == null) {
      mMain = new Main();
    }
    return mMain;
  }

  public void setMain(Main main) {
    mMain = main;
  }

  public List<Weather> getWeather() {
    return mWeather;
  }

  public void setWeather(List<Weather> weather) {
    mWeather = weather;
  }

  public Wind getWind() {
    if (mWind == null) {
      mWind = new Wind();
    }
    return mWind;
  }

  public void setWind(Wind wind) {
    mWind = wind;
  }

  public long getDt() {
    return mDt;
  }

  public void setDt(long dt) {
    mDt = dt;
  }

  public long getId() {
    return mId;
  }

  public void setId(long id) {
    mId = id;
  }

  public String getDateTime() {
    return mDateTime;
  }

  public void setDateTime(String dateTime) {
    this.mDateTime = dateTime;
  }
}
