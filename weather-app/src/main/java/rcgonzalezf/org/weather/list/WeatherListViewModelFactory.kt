package rcgonzalezf.org.weather.list

import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback

class WeatherListViewModelFactory(private val openWeatherApiCallback: OpenWeatherApiCallback,
                                  private val geoCoder: Geocoder) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherListViewModel(openWeatherApiCallback, geoCoder) as T
    }
}
