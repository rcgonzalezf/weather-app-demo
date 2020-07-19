package rcgonzalezf.org.weather.list

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.rcgonzalezf.weather.common.ServiceConfig
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.common.OnOfflineLoader
import rcgonzalezf.org.weather.common.ProgressIndicationStateChanger

class WeatherListViewModel(
        private val openWeatherApiCallback: OpenWeatherApiCallback,
        private val geoCoder: Geocoder,
        private val progressIndicationStateChanger: ProgressIndicationStateChanger,
        private val app: Application)
    : AndroidViewModel(app), OnOfflineLoader {

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
        progressIndicationStateChanger.toggleProgressIndicator()
        val weatherRepository = ServiceConfig.getInstance()
                .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
        weatherRepository.findWeather(
                OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
                        .withCityName(query)
                        .build(), openWeatherApiCallback)
        with(app.applicationContext) {
            Toast.makeText(this,
                    getString(R.string.searching) + " " + userInput + "...", Toast.LENGTH_SHORT)
                    .show()
        }
        updateCityNameForSwipeToRefresh(userInput)
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
