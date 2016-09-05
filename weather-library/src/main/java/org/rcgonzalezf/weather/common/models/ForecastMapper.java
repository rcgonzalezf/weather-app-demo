package org.rcgonzalezf.weather.common.models;

import java.util.ArrayList;
import java.util.List;

public class ForecastMapper {

  private static final int ALL = -1;
  private List<ForecastData> mData;
  // I've found that most cases the forecast consists of 40 items
  private static final int INITIAL_SIZE = 40;

  public ForecastMapper withData(List<ForecastData> data) {
    mData = data;
    return this;
  }

  public List<Forecast> map() {
    return map(ALL);
  }

  public List<Forecast> map(int howMany) {
    int size = 10;
    if(howMany == ALL) {
      size = INITIAL_SIZE * mData.size();
    }
    int counter = 0;
    List<Forecast> forecastList = new ArrayList<>(size);
    for (ForecastData forecastData : mData) {

      for (WeatherData weather : forecastData.getWeatherList()) {
        Forecast forecast = new Forecast();
        forecast.setCityId(forecastData.getCity().getId());
        forecast.setCityName(forecastData.getCity().getName());
        forecast.setSpeed(weather.getSpeed());
        forecast.setTemperature(weather.getTemp());
        forecast.setHumidity(String.valueOf(weather.getHumidity()));
        forecast.setDateTime(weather.getDateTime());
        forecast.setWeatherId(weather.getWeatherId());
        forecast.setCountry(forecastData.getCity().getCountry());
        forecast.setDeg(weather.getDeg());
        forecast.setDescription(weather.getDescription());

        forecastList.add(forecast);
        ++counter;
        if(counter == howMany) {
          return forecastList;
        }
      }
    }
    return forecastList;
  }
}
