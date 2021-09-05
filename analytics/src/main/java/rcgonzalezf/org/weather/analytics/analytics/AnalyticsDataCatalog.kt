package rcgonzalezf.org.weather.analytics.analytics

interface AnalyticsDataCatalog {
    interface WeatherListActivity {
        companion object {
            const val NO_NETWORK_SEARCH = "NO_NETWORK_SEARCH"
            const val MANUAL_SEARCH = "MANUAL_SEARCH"
            const val LOCATION_SEARCH = "LOCATION_SEARCH"
            const val SEARCH_COMPLETED = "SEARCH_COMPLETED"
        }
    }

    interface SettingsActivity {
        companion object {
            const val TEMP_UNITS_TOGGLE = "TEMP_UNITS_TOGGLE"
            const val ON_NAME = "ON_NAME"
            const val ON_LOAD = "ON_LOAD"
        }
    }
}
