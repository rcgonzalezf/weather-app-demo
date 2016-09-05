package rcgonzalezf.org.weather.common;

import java.util.List;
import org.rcgonzalezf.weather.common.models.Forecast;

public interface OnOfflineLoader {

  void loadOldData(List<Forecast> forecastList);
}
