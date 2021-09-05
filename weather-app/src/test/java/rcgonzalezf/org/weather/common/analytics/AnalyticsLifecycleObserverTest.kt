package rcgonzalezf.org.weather.common.analytics

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class AnalyticsLifecycleObserverTest {

    private val someScreenName: String = "someScreenName"
    lateinit var uut: rcgonzalezf.org.weather.analytics.analytics.AnalyticsLifecycleObserver
    @Mock
    lateinit var analytics: rcgonzalezf.org.weather.analytics.analytics.Analytics

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        uut = rcgonzalezf.org.weather.analytics.analytics.AnalyticsLifecycleObserver(
            someScreenName,
            analytics
        )
    }

    @Test
    fun trackOnScreenOnCreated() {
        uut.trackOnScreenOnCreated()

        verify(analytics).trackOnScreen(someScreenName)
    }

    @Test
    fun trackOnScreen() {
        uut.trackOnScreen()

        verify(analytics).trackOnScreen(someScreenName)
    }

    @Test
    fun trackOnActionEvent() {
        val analyticsEvent =
            rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(someScreenName)
        uut.trackOnActionEvent(analyticsEvent)

        verify(analytics).trackOnActionEvent(analyticsEvent)
    }
}
