package rcgonzalezf.org.weather.common.analytics;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.MANUAL_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED;

@StringDef({ LOCATION_SEARCH, NO_NETWORK_SEARCH, MANUAL_SEARCH, SEARCH_COMPLETED })
@Retention(RetentionPolicy.SOURCE) public @interface AnalyticsCatalog {
}
