package rcgonzalezf.org.weather.common;

import java.util.List;
import org.rcgonzalezf.weather.common.models.WeatherInfo;

public interface OnOfflineLoader {

  void loadOldData(List<WeatherInfo> weatherInfoList);
}
