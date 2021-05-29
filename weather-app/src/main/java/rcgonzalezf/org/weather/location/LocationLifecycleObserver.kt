package rcgonzalezf.org.weather.location

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LocationLifecycleObserver(val locationManager: LocationManager) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun connectListener() {
        locationManager.connect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun disconnectListener() {
        locationManager.disconnect()
    }
}
