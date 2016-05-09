package org.rcgonzalezf.weather.common.models.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.RawWeatherData;
import org.rcgonzalezf.weather.openweather.converter.IData;

public interface ModelConverter<T, E extends RawWeatherData, D extends IData> {

  T fromPojo(D pojo);

  T fromInputStream(InputStream inputStream);

  List<E> generateRawModel() throws IOException;

  List<ForecastData> getModel() throws IOException;
}
