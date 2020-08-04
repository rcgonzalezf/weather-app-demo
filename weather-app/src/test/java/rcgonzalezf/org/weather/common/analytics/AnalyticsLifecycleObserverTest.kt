package rcgonzalezf.org.weather.common.analytics

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class AnalyticsLifecycleObserverTest {

    private val someScreenName: String = "someScreenName"
    lateinit var uut:AnalyticsLifecycleObserver
    @Mock
    lateinit var analytics: Analytics

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        uut = AnalyticsLifecycleObserver(someScreenName, analytics)
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
        val analyticsEvent = AnalyticsEvent(someScreenName)
        uut.trackOnActionEvent(analyticsEvent)

        verify(analytics).trackOnActionEvent(analyticsEvent)
    }
}
