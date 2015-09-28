package org.rcgonzalezf.weather.openweather.models;

public class City {

  public final static String id_JSON = "id";
  public final static String name_JSON = "name";
  public final static String country_JSON = "country";

  private int id;
  private String name;
  private String country;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
