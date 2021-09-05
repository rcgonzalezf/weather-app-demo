package rcgonzalezf.org.weather.analytics.analytics

import javax.inject.Inject

open // for Mockito
class Analytics
// FIXME default  = WeatherApp.analyticsManager
@Inject constructor(private val analyticsManager: AnalyticsManager) {
    open fun trackOnScreen(screenName: String) {
        analyticsManager.notifyOnScreenLoad(screenName)
    }

    open fun trackOnActionEvent(event: AnalyticsEvent) {
        analyticsManager.notifyOnAction(event)
    }
}
