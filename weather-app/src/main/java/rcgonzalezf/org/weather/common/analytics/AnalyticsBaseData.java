package rcgonzalezf.org.weather.common.analytics;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsBaseData {

  private Map<String, String> mData = new HashMap<>();

  public Map<String, String> data() {
    return mData;
  }
}
