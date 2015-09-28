package rcgonzalezf.org.weather.models;

import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.WeatherData;

public class ForecastMapper {

  private List<ForecastData> mData;
  // I've found that most cases the forecast consists of 40 items
  private static final int INITIAL_SIZE = 40;

  public ForecastMapper withData(List<ForecastData> data) {
    mData = data;
    return this;
  }

  public List<Forecast> map() {
    List<Forecast> forecastList = new ArrayList<>(INITIAL_SIZE * mData.size());
    for (ForecastData forecastData : mData) {

      for (WeatherData weather : forecastData.getWeatherList()) {
        Forecast forecast = new Forecast();
        forecast.setCityId(forecastData.getCity().getId());
        forecast.setCityName(forecastData.getCity().getName());
        forecast.setSpeed(String.valueOf(weather.getSpeed()));
        forecast.setTemperature(String.valueOf(weather.getTemp()));
        forecast.setHumidity(String.valueOf(weather.getHumidity()));
        forecast.setDateTime(weather.getDateTime());
        forecast.setWeatherId(weather.getWeatherId());
        forecast.setCountry(forecastData.getCity().getCountry());

        forecastList.add(forecast);
      }
    }
    return forecastList;
  }
}
