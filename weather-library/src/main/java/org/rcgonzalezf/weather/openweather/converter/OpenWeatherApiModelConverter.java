package org.rcgonzalezf.weather.openweather.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;

public class OpenWeatherApiModelConverter implements ModelConverter<Void, OpenWeatherApiRawData> {

  private InputStream mInputStream;

  @Override public Void fromInputStream(InputStream inputStream) {
    mInputStream = inputStream;
    return null;
  }

  @Override public List<OpenWeatherApiRawData> generateRawModel() throws IOException {
    final OpenApiWeatherJsonParser parser
        = new OpenApiWeatherJsonParser();
    return parser.parseJsonStream(mInputStream);
  }
}
