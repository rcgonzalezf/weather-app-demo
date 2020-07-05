package rcgonzalezf.org.weather.common.analytics.observer

import android.util.Log
import rcgonzalezf.org.weather.common.analytics.AnalyticsBaseData
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.common.analytics.AnalyticsObserver

class LogcatAnalyticsObserver : AnalyticsObserver {

    companion object {
        private val TAG = LogcatAnalyticsObserver::class.java.name
    }

    override fun onScreen(screenName: String, analyticsBaseData: AnalyticsBaseData) {
        val builder = "onScreen: [ " +
                screenName +
                " | data: " +
                printableData(analyticsBaseData.data()) +
                " ]"
        Log.d(TAG, builder)
    }

    override fun onAction(analyticsEvent: AnalyticsEvent, screenName: String,
                          analyticsBaseData: AnalyticsBaseData) {
        val builder = "onAction: [ event:" +
                " name=" + analyticsEvent.name +
                " additionalValue=" + analyticsEvent.additionalValue +
                " | screenName, " +
                screenName +
                " | data: " +
                printableData(analyticsBaseData.data()) +
                " ]"
        Log.d(TAG, builder)
    }

    private fun printableData(data: Map<String, String>): StringBuilder {
        val stringBuilder = StringBuilder()
        for (key in data.keys) {
            stringBuilder.append("{").append(key).append(" | ").append(data[key]).append("}")
        }
        return stringBuilder
    }
}
