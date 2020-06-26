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

  private OpenWeatherForecastData mOpenWeatherForecastData;
  private OpenWeatherCurrentData mOpenWeatherCurrentData;

  @Override public void fromForecastPojo(OpenWeatherForecastData pojo) {
    mOpenWeatherForecastData = pojo;
  }

  @Override public void fromWeatherPojo(OpenWeatherCurrentData pojo) {
    mOpenWeatherCurrentData = pojo;
  }

  @Override public List<ForecastData> getForecastModel() throws IOException {

    List<ForecastData> forecastData = new LinkedList<>();
    if (mOpenWeatherForecastData != null) {
      forecastData = populateForecastFromPojo(forecastData);
    }
    return forecastData;
  }

  @Override public List<ForecastData> getWeatherModel() throws IOException {
    List<ForecastData> forecastData = new LinkedList<>();
    if (mOpenWeatherCurrentData != null) {
      forecastData = populateWeatherFromPojo(forecastData);
    }
    return forecastData;
  }

  private @Nullable
  List<ForecastData> populateWeatherFromPojo(List<ForecastData> forecastData) {
    if (mOpenWeatherCurrentData.getWeather() != null
        && mOpenWeatherCurrentData.getWeather().size() > 0) {

      for (Weather weather : mOpenWeatherCurrentData.getWeather()) {

        if (String.valueOf(HttpURLConnection.HTTP_OK)
            .equalsIgnoreCase(mOpenWeatherCurrentData.getCod())) {

          City city = new City();
          city.setName(mOpenWeatherCurrentData.getName());
          city.setCountry(mOpenWeatherCurrentData.getSys().country);

          ForecastData forecastDatum = new ForecastDataBuilder().setCity(city)
              .setCount(mOpenWeatherCurrentData.getWeather().size())
              .createForecastData();

          WeatherData weatherData = new WeatherData();
          String dateTime = formatDateTime(mOpenWeatherCurrentData.getDt());
          weatherData.setDateTime(dateTime);
          weatherData.setWeatherId(weather.getId());
          weatherData.setDescription(weather.getDescription());
          weatherData.setSpeed(mOpenWeatherCurrentData.getWind().getSpeed());
          weatherData.setDeg(mOpenWeatherCurrentData.getWind().getDeg());
          weatherData.setTemp(mOpenWeatherCurrentData.getMain().getTemp());
          weatherData.setHumidity(mOpenWeatherCurrentData.getMain().getHumidity());

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

    if (mOpenWeatherForecastData.getWeatherList() != null
        && mOpenWeatherForecastData.getWeatherList().size() > 0) {

      for (WeatherList weatherList : mOpenWeatherForecastData.getWeatherList()) {

        if (String.valueOf(HttpURLConnection.HTTP_OK)
            .equalsIgnoreCase(mOpenWeatherForecastData.getCod())) {
          ForecastData forecastDatum =
              new ForecastDataBuilder().setCity(mOpenWeatherForecastData.getCity())
                  .setCount(Integer.valueOf(mOpenWeatherForecastData.getCnt()))
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
