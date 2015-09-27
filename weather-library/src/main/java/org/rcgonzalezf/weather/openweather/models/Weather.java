package org.rcgonzalezf.weather.openweather.models;

public class Weather {
  public final static String id_JSON = "id";
  public final static String main_JSON = "main";
  public final static String description_JSON = "description";
  public final static String icon_JSON = "icon";

  private long id;
  private String main;
  private String description;
  private String icon;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getMain() {
    return main;
  }

  public void setMain(String main) {
    this.main = main;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}
