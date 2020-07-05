package rcgonzalezf.org.weather.common.analytics.observer

import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.WeatherApp
import rcgonzalezf.org.weather.common.analytics.AnalyticsBaseData
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager
import rcgonzalezf.org.weather.common.analytics.AnalyticsObserver

class GoogleAnalyticsObserver : AnalyticsObserver {
    private var mTracker: Tracker? = null

    companion object {
        private const val OS_VERSION = 1
        private const val APP_VERSION = 2
        private const val NETWORK = 3
        private const val MULTIPANE = 4
    }

    // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
    @get:Synchronized
    private val defaultTracker: Tracker?
        private get() {
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(WeatherApp.getInstance())
                // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
                mTracker = analytics.newTracker(R.xml.global_tracker)
                mTracker?.setAnonymizeIp(true)
            }
            return mTracker
        }

    override fun onScreen(screenName: String, analyticsBaseData: AnalyticsBaseData) {
        mTracker = defaultTracker
        mTracker?.setScreenName(screenName)
        mTracker?.send(ScreenViewBuilder().build())
    }

    override fun onAction(analyticsEvent: AnalyticsEvent, screenName: String,
                          analyticsBaseData: AnalyticsBaseData) {
        val builder = HitBuilders.EventBuilder().setCategory(analyticsEvent.name)
        if (analyticsEvent.additionalValue != null) {
            builder.setAction(analyticsEvent.additionalValue)
        }
        builder.setCustomDimension(OS_VERSION,
                analyticsBaseData.data()[AnalyticsManager.ANDROID_VERSION])
        builder.setCustomDimension(APP_VERSION,
                analyticsBaseData.data()[AnalyticsManager.APP_VERSION])
        builder.setCustomDimension(NETWORK, analyticsBaseData.data()[AnalyticsManager.NETWORK])
        builder.setCustomDimension(MULTIPANE,
                analyticsBaseData.data()[AnalyticsManager.MULTIPANE])

        mTracker = defaultTracker
        mTracker?.setScreenName(screenName)
        mTracker?.send(builder.build())
    }
}
