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

  final public static String cod_JSON = "cod";
  final public static String name_JSON = "name";
  final public static String id_JSON = "id";
  final public static String dt_JSON = "dt";
  final public static String wind_JSON = "wind";
  final public static String main_JSON = "main";
  final public static String base_JSON = "base";
  final public static String weather_JSON = "weather";
  final public static String sys_JSON = "sys";

  private Sys mSys;
  private String mBase;
  private Main mMain;
  private List<Weather> mWeather = new ArrayList<Weather>();
  private Wind mWind;
  private long mDt;
  private long mId;
  private String mName;
  private long mCod;

  public Sys getSys() {
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
}
