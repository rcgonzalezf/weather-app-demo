package org.rcgonzalezf.weather.openweather.model;

// Gson
@SuppressWarnings("unused") public class City {

  private int id;

  private String name;

  private String country;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCountry() {
    return country;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}

