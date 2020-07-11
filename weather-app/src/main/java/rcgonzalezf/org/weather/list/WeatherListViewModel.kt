package rcgonzalezf.org.weather.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherListViewModel: ViewModel() {

    val cityNameToSearchOnSwipe: MutableLiveData<CharSequence> by lazy {
        MutableLiveData<CharSequence>()
    }

    fun updateCityNameForSwipeToRefresh(cityName: CharSequence) {
        cityNameToSearchOnSwipe.value = cityName
    }
}
