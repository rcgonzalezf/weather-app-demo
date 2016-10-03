package org.rcgonzalezf.weather.openweather.network;

import org.rcgonzalezf.weather.common.network.RequestParameters;

public class OpenWeatherApiRequestParameters implements RequestParameters {

  private String lat;
  private String lon;
  private String cityName;

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  String getCityName() {
    return cityName;
  }

  private void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public static class OpenWeatherApiRequestBuilder {

    public static final String LON = "lon";
    public static final String LAT = "lat";
    public static final String TYPE = "type";
    static final String LIKE = "like";
    private final OpenWeatherApiRequestParameters mOpenWeatherApiRequestParameters;


    public OpenWeatherApiRequestBuilder() {
      mOpenWeatherApiRequestParameters = new OpenWeatherApiRequestParameters();
    }

    public OpenWeatherApiRequestParameters build() {
      return mOpenWeatherApiRequestParameters;
    }

    private OpenWeatherApiRequestBuilder withLat(Double lat) {
      mOpenWeatherApiRequestParameters.setLat(String.valueOf(lat));
      return this;
    }

    private OpenWeatherApiRequestBuilder withLon(Double lon) {
      mOpenWeatherApiRequestParameters.setLon(String.valueOf(lon));
      return this;
    }

    public OpenWeatherApiRequestBuilder withLatLon(Double lat, Double lon) {
      return withLat(lat).withLon(lon);
    }

    public OpenWeatherApiRequestBuilder withCityName(String cityName) {
      mOpenWeatherApiRequestParameters.setCityName(cityName);
      return this;
    }
  }
}
