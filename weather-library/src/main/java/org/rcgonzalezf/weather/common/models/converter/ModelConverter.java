package org.rcgonzalezf.weather.common.models.converter;

import java.io.IOException;
import java.util.List;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.openweather.converter.Data;

public interface ModelConverter<D extends Data> {

  void fromPojo(D pojo);

  List<ForecastData> getModel() throws IOException;
}
