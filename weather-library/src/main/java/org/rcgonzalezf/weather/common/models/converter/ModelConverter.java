package org.rcgonzalezf.weather.common.models.converter;

import java.io.IOException;
import java.util.List;

public interface ModelConverter<D extends Data, E, W extends Data> {

  void fromForecastPojo(D pojo);

  void fromWeatherPojo(W pojo);

  List<E> getForecastModel() throws IOException;

  List<E> getWeatherModel() throws IOException;
}
