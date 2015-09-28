package org.rcgonzalezf.weather.openweather.models;

import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.models.RawWeatherData;

public class OpenWeatherApiRawData implements RawWeatherData<OpenWeatherApiRawData> {

  public OpenWeatherApiRawData() {

  }

  public OpenWeatherApiRawData(Sys sys, String base, Main main, List<Weather> weather, Wind wind,
      long dt, long id, String name, long cod) {
    mSys = sys;
    mBase = base;
    mMain = main;
    mWeather = weather;
    mWind = wind;
    mDt = dt;
    mId = id;
    mName = name;
    mCod = cod;
  }

  @Override public OpenWeatherApiRawData get() {
    return this;
  }

  public final static String cod_JSON = "cod";
  public final static String name_JSON = "name";
  public final static String id_JSON = "id";
  public final static String dt_JSON = "dt";
  public final static String wind_JSON = "wind";
  public final static String main_JSON = "main";
  public final static String base_JSON = "base";
  public final static String weather_JSON = "weather";
  public final static String sys_JSON = "sys";
  public final static String city_JSON = "city";

  private Sys mSys;
  private String mBase;
  private Main mMain;
  private List<Weather> mWeather = new ArrayList<Weather>();
  private Wind mWind;
  private long mDt;
  private long mId;
  private String mName;
  private long mCod;
  private City mCity;

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

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

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
}
