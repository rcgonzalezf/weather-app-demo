package rcgonzalezf.org.weather.common.analytics;

public interface AnalyticsDataCatalog {
  interface WeatherListActivity {
    String NO_NETWORK_SEARCH = "NO_NETWORK_SEARCH";
    String MANUAL_SEARCH = "MANUAL_SEARCH";
    String LOCATION_SEARCH = "LOCATION_SEARCH";
    String SEARCH_COMPLETED = "SEARCH_COMPLETED";
  }

  interface SettingsActivity {
    String TEMP_UNITS_TOGGLE = "TEMP_UNITS_TOGGLE";
    String ON_NAME = "ON_NAME";
    String ON_LOAD = "ON_LOAD";
  }
}
