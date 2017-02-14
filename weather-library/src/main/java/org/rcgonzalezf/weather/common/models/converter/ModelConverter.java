package org.rcgonzalezf.weather.common.models.converter;

import java.io.IOException;
import java.util.List;

public interface ModelConverter<D extends Data> {

  void fromPojo(D pojo);

  List<Data> getModel() throws IOException;
}
