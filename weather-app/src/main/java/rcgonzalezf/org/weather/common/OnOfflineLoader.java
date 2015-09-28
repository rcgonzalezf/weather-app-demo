package rcgonzalezf.org.weather.common;

import java.util.List;
import rcgonzalezf.org.weather.models.Forecast;

public interface OnOfflineLoader {

  void loadOldData(List<Forecast> forecastList);
}
