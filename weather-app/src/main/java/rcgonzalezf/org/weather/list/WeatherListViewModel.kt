package rcgonzalezf.org.weather.list

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.rcgonzalezf.weather.common.ServiceConfig
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters
import rcgonzalezf.org.weather.common.OnOfflineLoader

class WeatherListViewModel(private val openWeatherApiCallback:OpenWeatherApiCallback,
                           private val geoCoder: Geocoder): ViewModel(), OnOfflineLoader {

    companion object {
        private val TAG = WeatherListViewModel::class.java.simpleName
    }

    val cityNameToSearchOnSwipe: MutableLiveData<CharSequence> by lazy {
        MutableLiveData<CharSequence>()
    }

    val weatherInfoList: MutableLiveData<List<WeatherInfo>> by lazy {
        MutableLiveData<List<WeatherInfo>>()
    }

    val offline: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun updateCityNameForSwipeToRefresh(cityName: CharSequence) {
        cityNameToSearchOnSwipe.value = cityName
    }

    override fun loadOldData(weatherInfoList: List<WeatherInfo>?) {
        this.weatherInfoList.value = weatherInfoList
        this.offline.value = true
    }

    fun searchByQuery(query: String, userInput: CharSequence) {
        val weatherRepository = ServiceConfig.getInstance()
                .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
        weatherRepository.findWeather(
                OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
                        .withCityName(query)
                        .build(), openWeatherApiCallback)
    }


    fun cityNameFromLatLon(lat: Double, lon: Double): String? {
        var cityName: String? = null
        val addresses: List<Address>
        try {
            addresses = geoCoder.getFromLocation(lat, lon, 1)
            cityName = addresses[0].locality
        } catch (e: Exception) {
            Log.d(TAG, "error retrieving the cityName with Geocoder")
        }
        return cityName
    }
}
