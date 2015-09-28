package org.rcgonzalezf.weather.openweather.converter;

import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.openweather.models.City;
import org.rcgonzalezf.weather.openweather.models.Main;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;
import org.rcgonzalezf.weather.openweather.models.RawListItem;
import org.rcgonzalezf.weather.openweather.models.Sys;
import org.rcgonzalezf.weather.openweather.models.Weather;
import org.rcgonzalezf.weather.openweather.models.Wind;

public class OpenApiWeatherJsonParser {

  public List<OpenWeatherApiRawData> parseJsonStream(InputStream inputStream) throws IOException {
    JsonReader reader = null;
    try {
      reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
      return parseJsonWeatherArray(reader);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  public List<OpenWeatherApiRawData> parseJsonWeatherArray(JsonReader reader) throws IOException {
    List<OpenWeatherApiRawData> jsonWeatherList = new ArrayList<>();
    jsonWeatherList.add(parseJsonWeather(reader));
    return jsonWeatherList;
  }

  public OpenWeatherApiRawData parseJsonWeather(JsonReader reader) throws IOException {
    OpenWeatherApiRawData openWeatherApiRawData = null;
    reader.beginObject();

    try {
      while (reader.hasNext()) {
        if (openWeatherApiRawData == null) {
          openWeatherApiRawData = new OpenWeatherApiRawData();
        }

        String name = reader.nextName();
        switch (name) {
          case OpenWeatherApiRawData.city_JSON:
            openWeatherApiRawData.setCity(parseCity(reader));
            break;
          case OpenWeatherApiRawData.cod_JSON:
            openWeatherApiRawData.setCod(reader.nextInt());
            break;
          case OpenWeatherApiRawData.cnt_JSON:
            openWeatherApiRawData.setCount(reader.nextInt());
            break;
          case OpenWeatherApiRawData.list_JSON:
            openWeatherApiRawData.setRawList(parseRawList(reader));
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return openWeatherApiRawData;
  }

  private List<RawListItem> parseRawList(JsonReader reader) throws IOException {
    List<RawListItem> rawListItems = new ArrayList<>();
    reader.beginArray();
    try {
      while (reader.hasNext()) {
        rawListItems.add(parseRawItem(reader));
      }
    } finally {
      reader.endArray();
    }
    return rawListItems;

  }

  private RawListItem parseRawItem(JsonReader reader) throws IOException {

    RawListItem rawListItem = new RawListItem();
    reader.beginObject();
    try {
      while (reader.hasNext()) {
        String name = reader.nextName();
        switch (name) {
          case RawListItem.dt_JSON:
            rawListItem.setDt(reader.nextLong());
            break;
          case RawListItem.main_JSON:
            rawListItem.setMain(parseMain(reader));
            break;
          case RawListItem.weather_JSON:
            rawListItem.setWeather(parseWeathers(reader));
            break;
          case RawListItem.wind_JSON:
            rawListItem.setWind(parseWind(reader));
            break;
          case RawListItem.sys_JSON:
            rawListItem.setSys(parseSys(reader));
            break;
          case RawListItem.dt_txt_JSON:
            rawListItem.setDateTime(reader.nextString());
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return rawListItem;
  }

  public List<Weather> parseWeathers(JsonReader reader) throws IOException {
    List<Weather> weatherList = new ArrayList<>();
    reader.beginArray();
    try {
      while (reader.hasNext()) {
        weatherList.add(parseWeather(reader));
      }
    } finally {
      reader.endArray();
    }
    return weatherList;
  }

  public Weather parseWeather(JsonReader reader) throws IOException {

    Weather weather = new Weather();

    reader.beginObject();
    try {
      while (reader.hasNext()) {
        String name = reader.nextName();
        switch (name) {
          case Weather.id_JSON:
            weather.setId(reader.nextInt());
            break;
          case Weather.description_JSON:
            weather.setDescription(reader.nextString());
            break;
          case Weather.icon_JSON:
            weather.setIcon(reader.nextString());
            break;
          case Weather.main_JSON:
            weather.setMain(reader.nextString());
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return weather;
  }

  public Main parseMain(JsonReader reader) throws IOException {

    Main main = new Main();
    reader.beginObject();
    try {
      while (reader.hasNext()) {
        String name = reader.nextName();
        switch (name) {
          case Main.temp_JSON:
            main.setTemp(reader.nextDouble());
            break;
          case Main.humidity_JSON:
            main.setHumidity(reader.nextLong());
            break;
          case Main.tempMin_JSON:
            main.setTempMin(reader.nextDouble());
            break;
          case Main.tempMax_JSON:
            main.setTempMax(reader.nextDouble());
            break;
          case Main.pressure_JSON:
            main.setPressure(reader.nextDouble());
            break;
          case Main.seaLevel_JSON:
            main.setSeaLevel(reader.nextDouble());
            break;
          case Main.grndLevel_JSON:
            main.setGrndLevel(reader.nextDouble());
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return main;
  }

  public Wind parseWind(JsonReader reader) throws IOException {

    Wind wind = new Wind();
    reader.beginObject();

    try {
      while (reader.hasNext()) {
        String name = reader.nextName();
        switch (name) {
          case Wind.speed_JSON:
            wind.setSpeed(reader.nextDouble());
            break;
          case Wind.deg_JSON:
            wind.setDeg(reader.nextDouble());
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return wind;
  }

  public City parseCity(JsonReader reader) throws IOException {

    City city = new City();
    reader.beginObject();

    try {
      while (reader.hasNext()) {
        String name = reader.nextName();
        switch (name) {
          case City.name_JSON:
            city.setName(reader.nextString());
            break;
          case City.id_JSON:
            city.setId(reader.nextInt());
            break;
          case City.country_JSON:
            city.setCountry(reader.nextString());
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return city;
  }


  public Sys parseSys(JsonReader reader) throws IOException {
    Sys sys = new Sys();
    reader.beginObject();
    try {
      while (reader.hasNext()) {
        String name = reader.nextName();
        switch (name) {
          case Sys.sunrise_JSON:
            sys.setSunrise(reader.nextLong());
            break;
          case Sys.sunset_JSON:
            sys.setSunset(reader.nextLong());
            break;
          case Sys.country_JSON:
            sys.setCountry(reader.nextString());
            break;
          case Sys.message_JSON:
            sys.setMessage(reader.nextDouble());
            break;
          default:
            reader.skipValue();
        }
      }
    } finally {
      reader.endObject();
    }

    return sys;
  }
}
