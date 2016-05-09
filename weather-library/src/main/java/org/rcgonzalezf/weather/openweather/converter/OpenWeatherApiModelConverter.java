package org.rcgonzalezf.weather.openweather.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.ForecastDataBuilder;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.openweather.model.Weather;
import org.rcgonzalezf.weather.openweather.model.WeatherList;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;

public class OpenWeatherApiModelConverter
    implements ModelConverter<Void, OpenWeatherApiRawData, OpenWeatherForecastData> {

  private OpenWeatherForecastData mOpenWeatherForecastData;

  @Override public Void fromPojo(OpenWeatherForecastData pojo) {
    mOpenWeatherForecastData = pojo;
    return null;
  }

  @Override public Void fromInputStream(@NonNull InputStream inputStream) {
    throw new UnsupportedOperationException("not supported");
  }

  @Override public @Nullable List<OpenWeatherApiRawData> generateRawModel() throws IOException {
    throw new UnsupportedOperationException("not supported");
  }

  @Override public List<ForecastData> getModel() throws IOException {

    List<ForecastData> forecastData = null;
    if (mOpenWeatherForecastData != null) {
      forecastData = populateFromPojo(forecastData);
    }

    return forecastData;
  }

  protected List<ForecastData> populateFromPojo(List<ForecastData> forecastData) {

    if (mOpenWeatherForecastData.getWeatherList() != null
        && mOpenWeatherForecastData.getWeatherList().size() > 0) {
      forecastData = new ArrayList<>(mOpenWeatherForecastData.getWeatherList().size());

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
