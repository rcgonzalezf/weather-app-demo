package org.rcgonzalezf.weather.openweather.network;

public enum Units {
  METRIC("metric"),
  IMPERIAL("imperial");

  private final String mUnitName;

  Units(String unitName) {
    mUnitName = unitName;
  }

  public String getUnitName() {
    return mUnitName;
  }
}
