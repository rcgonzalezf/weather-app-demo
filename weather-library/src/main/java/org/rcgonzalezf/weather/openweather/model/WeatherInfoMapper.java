package org.rcgonzalezf.weather.openweather.model;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.models.WeatherInfo;

public class WeatherInfoMapper {

  private static final int ALL = -1;
  private List<ForecastData> mData;
  // I've found that most cases the forecast consists of 40 items
  private static final int INITIAL_SIZE = 40;

  public WeatherInfoMapper withData(@NonNull List<ForecastData> data) {
    mData = data;
    return this;
  }

  public @NonNull List<WeatherInfo> map() {
    return map(ALL);
  }

  @VisibleForTesting @NonNull List<WeatherInfo> map(int howMany) {
    int size = 10;
    if (howMany == ALL) {
      size = INITIAL_SIZE * mData.size();
    }
    int counter = 0;
    List<WeatherInfo> weatherInfoList = new ArrayList<>(size);
    for (ForecastData forecastData : mData) {

      for (WeatherData weather : forecastData.getWeatherList()) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setCityId(forecastData.getCity().getId());
        weatherInfo.setCityName(forecastData.getCity().getName());
        weatherInfo.setSpeed(weather.getSpeed());
        weatherInfo.setTemperature(weather.getTemp());
        weatherInfo.setHumidity(String.valueOf(weather.getHumidity()));
        weatherInfo.setDateTime(weather.getDateTime());
        weatherInfo.setWeatherId(weather.getWeatherId());
        weatherInfo.setCountry(forecastData.getCity().getCountry() == null ? "" : forecastData.getCity().getCountry());
        weatherInfo.setDeg(weather.getDeg());
        weatherInfo.setDescription(weather.getDescription());

        weatherInfoList.add(weatherInfo);
        ++counter;
        if (counter == howMany) {
          return weatherInfoList;
        }
      }
    }
    return weatherInfoList;
  }
}
