package rcgonzalezf.org.weather.common.analytics

import rcgonzalezf.org.weather.WeatherApp

class Analytics(private val analyticsManager: AnalyticsManager = WeatherApp.analyticsManager) {
    fun trackOnScreen(screenName: String) {
        analyticsManager.notifyOnScreenLoad(screenName)
    }

    fun trackOnActionEvent(event: AnalyticsEvent) {
        analyticsManager.notifyOnAction(event)
    }
}
