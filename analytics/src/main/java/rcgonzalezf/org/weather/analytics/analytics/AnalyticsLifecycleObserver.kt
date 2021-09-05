package rcgonzalezf.org.weather.analytics.analytics

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
open // for Mockito
class AnalyticsLifecycleObserver
    @Inject constructor(private val screenName: String, val analytics: Analytics)
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
