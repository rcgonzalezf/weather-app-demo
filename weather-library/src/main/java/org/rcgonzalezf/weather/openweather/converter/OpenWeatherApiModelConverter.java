package org.rcgonzalezf.weather.openweather.converter;

import androidx.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.model.City;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.ForecastDataBuilder;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.openweather.model.Weather;
import org.rcgonzalezf.weather.openweather.model.WeatherData;
import org.rcgonzalezf.weather.openweather.model.WeatherList;

public class OpenWeatherApiModelConverter
    implements ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> {

  private OpenWeatherForecastData openWeatherForecastData;
  private OpenWeatherCurrentData openWeatherCurrentData;

  @Override public void fromForecastPojo(OpenWeatherForecastData pojo) {
    openWeatherForecastData = pojo;
  }

  @Override public void fromWeatherPojo(OpenWeatherCurrentData pojo) {
    openWeatherCurrentData = pojo;
  }

  @Override public List<ForecastData> getForecastModel() throws IOException {

    List<ForecastData> forecastData = new LinkedList<>();
    if (openWeatherForecastData != null) {
      forecastData = populateForecastFromPojo(forecastData);
    }
    return forecastData;
  }

  @Override public List<ForecastData> getWeatherModel() throws IOException {
    List<ForecastData> forecastData = new LinkedList<>();
    if (openWeatherCurrentData != null) {
      forecastData = populateWeatherFromPojo(forecastData);
    }
    return forecastData;
  }

  private @Nullable
  List<ForecastData> populateWeatherFromPojo(List<ForecastData> forecastData) {
    if (openWeatherCurrentData.getWeather() != null
        && openWeatherCurrentData.getWeather().size() > 0) {

      for (Weather weather : openWeatherCurrentData.getWeather()) {

        if (String.valueOf(HttpURLConnection.HTTP_OK)
            .equalsIgnoreCase(openWeatherCurrentData.getCod())) {

          City city = new City();
          city.setName(openWeatherCurrentData.getName());
          city.setCountry(openWeatherCurrentData.getSys().country);

          ForecastData forecastDatum = new ForecastDataBuilder().setCity(city)
              .setCount(openWeatherCurrentData.getWeather().size())
              .createForecastData();

          WeatherData weatherData = new WeatherData();
          String dateTime = formatDateTime(openWeatherCurrentData.getDt());
          weatherData.setDateTime(dateTime);
          weatherData.setWeatherId(weather.getId());
          weatherData.setDescription(weather.getDescription());
          weatherData.setSpeed(openWeatherCurrentData.getWind().getSpeed());
          weatherData.setDeg(openWeatherCurrentData.getWind().getDeg());
          weatherData.setTemp(openWeatherCurrentData.getMain().getTemp());
          weatherData.setHumidity(openWeatherCurrentData.getMain().getHumidity());

          forecastDatum.addWeatherItem(weatherData);

          forecastData.add(forecastDatum);
        }
      }
    }
    return forecastData;
  }

  private String formatDateTime(long unixDateTime) {
    Date date = new Date();
    date.setTime(unixDateTime * 1000);

    //2014-07-23 09:00:00
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    return dateFormat.format(date);
  }

  private @Nullable List<ForecastData> populateForecastFromPojo(List<ForecastData> forecastData) {

    if (openWeatherForecastData.getWeatherList() != null
        && openWeatherForecastData.getWeatherList().size() > 0) {

      for (WeatherList weatherList : openWeatherForecastData.getWeatherList()) {

        if (String.valueOf(HttpURLConnection.HTTP_OK)
            .equalsIgnoreCase(openWeatherForecastData.getCod())) {
          ForecastData forecastDatum =
              new ForecastDataBuilder().setCity(openWeatherForecastData.getCity())
                  .setCount(Integer.valueOf(openWeatherForecastData.getCnt()))
                  .createForecastData();

          for (Weather weather : weatherList.getWeather()) {
            WeatherData weatherData = new WeatherData();
            weatherData.setDateTime(weatherList.getDt_txt());
            weatherData.setWeatherId(weather.getId());
            weatherData.setDescription(weather.getDescription());
            weatherData.setSpeed(weatherList.getWind().getSpeed());
            weatherData.setDeg(weatherList.getWind().getDeg());
            weatherData.setTemp(weatherList.getMain().getTemp());
            weatherData.setHumidity(weatherList.getMain().getHumidity());

            forecastDatum.addWeatherItem(weatherData);
          }

          forecastData.add(forecastDatum);
        }
      }
    }
    return forecastData;
  }
}
