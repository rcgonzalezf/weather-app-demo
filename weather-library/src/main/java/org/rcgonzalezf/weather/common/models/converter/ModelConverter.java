package org.rcgonzalezf.weather.common.models.converter;

import java.io.IOException;
import java.util.List;

public interface ModelConverter<D extends Data, E> {

  void fromPojo(D pojo);

  List<E> getForecastModel() throws IOException;
}
