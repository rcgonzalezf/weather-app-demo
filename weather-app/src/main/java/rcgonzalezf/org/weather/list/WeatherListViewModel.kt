package rcgonzalezf.org.weather.list

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.rcgonzalezf.weather.common.ServiceConfig
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.common.OnOfflineLoader
import rcgonzalezf.org.weather.common.ToggleBehavior
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.common.analytics.AnalyticsLifecycleObserver
import rcgonzalezf.org.weather.location.CityFromLatLongRetriever
import rcgonzalezf.org.weather.location.LocationSearch
import rcgonzalezf.org.weather.utils.WeatherUtils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class WeatherListViewModel(
        private val openWeatherApiCallback: OpenWeatherApiCallback,
        private val cityNameFromLatLong: CityFromLatLongRetriever,
        private val toggleBehavior: ToggleBehavior,
        private val app: Application)
    : AndroidViewModel(app), OnOfflineLoader {

    companion object {
        private val TAG = WeatherListViewModel::class.java.simpleName
        const val OFFLINE_FILE = "OFFLINE_WEATHER"
        const val FORECASTS = "FORECASTS"
    }

    private val executor: Executor = Executors.newSingleThreadExecutor()

    val cityNameToSearchOnSwipe: MutableLiveData<CharSequence> by lazy {
        MutableLiveData<CharSequence>()
    }

    val weatherInfoList: MutableLiveData<List<WeatherInfo>> by lazy {
        MutableLiveData<List<WeatherInfo>>()
    }

    val offline: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val previousForecastList: List<WeatherInfo>?
        get() {
            // TODO Room and LiveData?
            val sharedPreferences = app.getSharedPreferences(OFFLINE_FILE, 0)
            val serializedData = sharedPreferences.getString(FORECASTS, null)
            var storedData: List<WeatherInfo>? = null
            if (serializedData != null) {
                storedData = Gson()
                        .fromJson(serializedData, object : TypeToken<List<WeatherInfo?>?>() {}.type)
            }
            return storedData
        }

    fun updateCityNameForSwipeToRefresh(cityName: CharSequence) {
        cityNameToSearchOnSwipe.value = cityName
    }

    override fun loadOldData(weatherInfoList: List<WeatherInfo>?) {
        this.weatherInfoList.value = weatherInfoList
        this.offline.value = true
    }

    fun searchByQuery(query: String, userInput: CharSequence) {
        toggleBehavior.toggle()
        val weatherRepository = ServiceConfig.getInstance()
                .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
        weatherRepository.findWeather(
                OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
                        .withCityName(query)
                        .build(), openWeatherApiCallback)
        with(app) {
            Toast.makeText(this,
                    getString(R.string.searching) + " " + userInput + "...", Toast.LENGTH_SHORT)
                    .show()
        }
        updateCityNameForSwipeToRefresh(userInput)
    }

    fun cityNameFromLatLon(lat: Double, lon: Double): String? {
        return cityNameFromLatLong.getFromLatLong(lat, lon)
    }

    fun saveForecastList(weatherInfoList: List<WeatherInfo>) {
        executor.execute {
            // TODO Replace with Room?
            val prefs = app.getSharedPreferences(OFFLINE_FILE, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(FORECASTS, Gson().toJson(weatherInfoList))
            editor.apply()
        }
    }

    @VisibleForTesting
    fun searchByManualInput(userInput: CharSequence) {
        val query: String
        query = try {
            URLEncoder.encode(userInput.toString(), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Can't encode URL", e)
            Toast.makeText(app, "${app.getString(R.string.invalid_input)}: $userInput...",
                    Toast.LENGTH_SHORT).show()
            return
        }
        if (!WeatherUtils.hasInternetConnection(app)) {
            Toast.makeText(app, app.getString(R.string.no_internet_msg),
                    Toast.LENGTH_SHORT).show()
            loadOldData(previousForecastList)
        } else {
            offline.value = false
            searchByQuery(query, userInput)
        }
    }

    inner class WeatherListLocationSearch(val analytics: AnalyticsLifecycleObserver) : LocationSearch {
        override fun searchByLatLon(lat: Double, lon: Double) {
            toggleBehavior.toggle()
            val weatherRepository = ServiceConfig.getInstance()
                    .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
            val cityName = cityNameFromLatLon(lat, lon)
            if (cityName == null) {
                weatherRepository.findWeather(
                        OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withLatLon(lat, lon).build(),
                        openWeatherApiCallback)
                analytics.trackOnActionEvent(
                        AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH,
                                "Geocoder Failure"))
            } else {
                weatherRepository.findWeather(
                        OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityName(cityName).build(),
                        openWeatherApiCallback)
                analytics.trackOnActionEvent(
                        AnalyticsEvent(
                                AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH, cityName))
                updateCityNameForSwipeToRefresh(cityName)
            }
        }
    }
}
