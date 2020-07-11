package rcgonzalezf.org.weather.location

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LocationLifecycleObserverTest {

    lateinit var uut:LocationLifecycleObserver
    @Mock
    lateinit var locationManager:LocationManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        uut = LocationLifecycleObserver(locationManager)
    }

    @Test
    fun connectListener() {
        whenConnectingLocationObserver()

        thenLocationManagerShouldBeConnected()
    }

    @Test
    fun disconnectListener() {
        whenDisconnectingTheListener()

        thenLocationManagerShouldBeDisconnected()
    }

    private fun thenLocationManagerShouldBeConnected() {
        Mockito.verify(locationManager, Mockito.atLeastOnce()).connect()
    }

    private fun whenConnectingLocationObserver() {
        uut.connectListener()
    }

    private fun thenLocationManagerShouldBeDisconnected() {
        Mockito.verify(locationManager, Mockito.atLeastOnce()).disconnect()
    }

    private fun whenDisconnectingTheListener() {
        uut.disconnectListener()
    }
}
