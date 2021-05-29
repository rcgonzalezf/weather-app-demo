package rcgonzalezf.org.weather.common.analytics

import rcgonzalezf.org.weather.WeatherApp

open // for Mockito
class Analytics(private val analyticsManager: AnalyticsManager = WeatherApp.analyticsManager) {
    open fun trackOnScreen(screenName: String) {
        analyticsManager.notifyOnScreenLoad(screenName)
    }

    open fun trackOnActionEvent(event: AnalyticsEvent) {
        analyticsManager.notifyOnAction(event)
    }
}
