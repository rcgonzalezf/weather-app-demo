package rcgonzalezf.org.weather.common.analytics;

public interface AnalyticsDataCatalog {
  interface WeatherListActivity {
    String NO_NETWORK_SEARCH = "NO_NETWORK_SEARCH";
    String MANUAL_SEARCH = "MANUAL_SEARCH";
    String LOCATION_SEARCH = "LOCATION_SEARCH";
    String SEARCH_COMPLETED = "SEARCH_COMPLETED";
  }
}
