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
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;

public class OpenWeatherApiModelConverter implements ModelConverter<Void, OpenWeatherApiRawData> {

  private InputStream mInputStream;

  @Override public Void fromInputStream(@NonNull InputStream inputStream) {
    mInputStream = inputStream;
    return null;
  }

  @Override public @Nullable List<OpenWeatherApiRawData> generateRawModel() throws IOException {
    final OpenApiWeatherJsonParser parser = new OpenApiWeatherJsonParser();
    return parser.parseJsonStream(mInputStream);
  }

  @Override public List<ForecastData> getModel() throws IOException {

    List<OpenWeatherApiRawData> rawModelList = generateRawModel();
    List<ForecastData> forecastData = null;

    if (rawModelList != null && rawModelList.size() > 0) {
      forecastData = new ArrayList<>(rawModelList.size());
      for (OpenWeatherApiRawData rawData : rawModelList) {

        if (rawData.getCod() == HttpURLConnection.HTTP_OK) {
          ForecastData forecastDatum = new ForecastDataBuilder()
              .setCity(rawData.getCity())
              .setSpeed(rawData.getWind().getSpeed())
              .setDeg(rawData.getWind().getDeg())
              .setTemp(rawData.getMain().getTemp())
              .setHumidity(rawData.getMain().getHumidity())
              .setSunrise(rawData.getSys().getSunrise())
              .setSunset(rawData.getSys().getSunset())
              .createForecastData();
          forecastData.add(forecastDatum);
        }
      }
    }
    return forecastData;
  }
}
