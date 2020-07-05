package rcgonzalezf.org.weather.common.analytics

import rcgonzalezf.org.weather.WeatherApp

class Analytics {
    fun trackOnScreen(screenName: String) {
        WeatherApp.getAnalyticsManager().notifyOnScreenLoad(screenName)
    }

    fun trackOnActionEvent(event: AnalyticsEvent) {
        WeatherApp.getAnalyticsManager().notifyOnAction(event)
    }
}
