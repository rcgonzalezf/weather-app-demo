package rcgonzalezf.org.weather.common.analytics

import androidx.annotation.StringDef
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.Companion.ON_LOAD
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.Companion.ON_NAME
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.Companion.TEMP_UNITS_TOGGLE
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.Companion.LOCATION_SEARCH
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.Companion.MANUAL_SEARCH
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.Companion.NO_NETWORK_SEARCH
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.Companion.SEARCH_COMPLETED

@StringDef(LOCATION_SEARCH, NO_NETWORK_SEARCH, MANUAL_SEARCH,
        SEARCH_COMPLETED, TEMP_UNITS_TOGGLE, ON_NAME, ON_LOAD)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class AnalyticsCatalog
