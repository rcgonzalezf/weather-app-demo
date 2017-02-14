package org.rcgonzalezf.weather.openweather.converter;

import android.support.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import org.rcgonzalezf.weather.common.models.converter.Data;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.ForecastDataBuilder;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.openweather.model.Weather;
import org.rcgonzalezf.weather.openweather.model.WeatherData;
import org.rcgonzalezf.weather.openweather.model.WeatherList;

public class OpenWeatherApiModelConverter implements ModelConverter<OpenWeatherForecastData> {

  private OpenWeatherForecastData mOpenWeatherForecastData;

  @Override public void fromPojo(OpenWeatherForecastData pojo) {
    mOpenWeatherForecastData = pojo;
  }

  @Override public List<Data> getModel() throws IOException {

    List<Data> forecastData = new LinkedList<>();
    if (mOpenWeatherForecastData != null) {
      forecastData = populateFromPojo(forecastData);
    }
    return forecastData;
  }

  private @Nullable List<Data> populateFromPojo(List<Data> forecastData) {

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
