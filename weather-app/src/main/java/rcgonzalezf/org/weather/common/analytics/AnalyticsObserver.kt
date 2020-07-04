package rcgonzalezf.org.weather.common.analytics

interface AnalyticsObserver {
    fun onScreen(screenName: String?, analyticsBaseData: AnalyticsBaseData?)
    fun onAction(analyticsEvent: AnalyticsEvent?, screenName: String?, analyticsBaseData: AnalyticsBaseData?)
}
