package rcgonzalezf.org.weather.common.analytics

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

open // for Mockito
class AnalyticsLifecycleObserver(private val screenName: String, val analytics: Analytics)
    : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun trackOnScreenOnCreated() {
        trackOnScreen()
    }

    fun trackOnScreen() {
        analytics.trackOnScreen(screenName)
    }

    open // for Mockito
    fun trackOnActionEvent(event: AnalyticsEvent) {
        analytics.trackOnActionEvent(event)
    }
}
