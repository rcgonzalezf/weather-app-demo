package rcgonzalezf.org.weather.list

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import rcgonzalezf.org.weather.common.ToggleBehavior

class WeatherListViewModelFactory(
        private val openWeatherApiCallback: OpenWeatherApiCallback, private val geoCoder: Geocoder,
        private val toggleBehavior: ToggleBehavior, private val app: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherListViewModel(openWeatherApiCallback, geoCoder,
                toggleBehavior, app) as T
    }
}
