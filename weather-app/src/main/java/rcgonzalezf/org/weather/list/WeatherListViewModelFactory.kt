package rcgonzalezf.org.weather.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import rcgonzalezf.org.weather.common.ToggleBehavior
import rcgonzalezf.org.weather.location.CityFromLatLongRetriever
import rcgonzalezf.org.weather.utils.UserNotifier

class WeatherListViewModelFactory(
        private val openWeatherApiCallback: OpenWeatherApiCallback,
        private val cityFromLatLongRetriever: CityFromLatLongRetriever,
        private val toggleBehavior: ToggleBehavior, private val app: Application,
        private val userNotifier: UserNotifier)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherListViewModel(openWeatherApiCallback, cityFromLatLongRetriever,
                toggleBehavior, app, userNotifier) as T
    }
}
