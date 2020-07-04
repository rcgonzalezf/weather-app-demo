package rcgonzalezf.org.weather

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import org.rcgonzalezf.weather.common.ServiceConfig
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.common.models.WeatherViewModel
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder
import rcgonzalezf.org.weather.R.id
import rcgonzalezf.org.weather.R.string
import rcgonzalezf.org.weather.adapters.ModelAdapter
import rcgonzalezf.org.weather.adapters.ModelAdapter.OnItemClickListener
import rcgonzalezf.org.weather.common.BaseActivity
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent
import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class WeatherListActivity : BaseActivity(), OnItemClickListener<WeatherViewModel>,
        OnUpdateWeatherListListener {
    private var recyclerView: RecyclerView? = null
    private var swipeToRefreshLayout: SwipeRefreshLayout? = null
    private var adapter: ModelAdapter<WeatherInfo>? = null
    private var openWeatherApiCallback: OpenWeatherApiCallback? = null
    private var cityNameToSearchOnSwipe: CharSequence? = null
    private var progress: ProgressBar? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openWeatherApiCallback = OpenWeatherApiCallback(this)
        setupRecyclerView()
        cityNameToSearchOnSwipe = savedInstanceState?.getCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE)
        progress = findViewById(id.progress_bar)
        swipeToRefreshLayout = findViewById(id.swipe_to_refresh_layout)
        enableSwipeToRefreshLayout()
        swipeToRefreshLayout?.setOnRefreshListener(createSwipeToRefreshListener())
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE, cityNameToSearchOnSwipe)
    }

    @VisibleForTesting
    fun onItemsLoadComplete() {
        toggleProgressIndicator()
        enableSwipeToRefreshLayout()
        swipeToRefreshLayout?.let {
            if (it.isRefreshing) {
                it.isRefreshing = false
            }
        }
    }

    @VisibleForTesting
    fun toggleProgressIndicator() {
        progress?.let {
            it.visibility = if (it.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        recyclerView?.scheduleLayoutAnimation()
    }

    override fun onItemClick(view: View, viewModel: WeatherViewModel) {
        Toast.makeText(this,
                String.format(
                        getString(string.item_clicked_debug_format), viewModel.id,
                        viewModel.dateTime, viewModel.description),
                Toast.LENGTH_SHORT)
                .show()
    }

    override fun loadOldData(weatherInfoList: List<WeatherInfo>?) {
        if (weatherInfoList != null && weatherInfoList.isNotEmpty()) {
            notifyAdapter(weatherInfoList)
            trackOnActionEvent(
                    AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH,
                            weatherInfoList[0].cityName))
        } else {
            Log.d(TAG, "No data even in offline mode :(")
            trackOnActionEvent(
                    AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH,
                            "EMPTY"))
            //cancel swipe to refresh loading
            onItemsLoadComplete()
        }
    }

    override fun updateList(weatherInfoList: List<WeatherInfo>) {
        val cityName = if (weatherInfoList.isEmpty()) "" else weatherInfoList[0].cityName
        trackOnActionEvent(
                AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED,
                        "cityName: $cityName"))
        notifyAdapter(weatherInfoList)
    }

    override fun onError(error: String) {
        // TODO implement error handling
        runOnUiThread(createRunnableToggleProgressIndicator())
        Log.d(TAG, error)
        trackOnActionEvent(
                AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED,
                        "error: $error"))
    }

    public override fun searchByQuery(query: String, userInput: CharSequence) {
        toggleProgressIndicator()
        val weatherRepository = ServiceConfig.getInstance()
                .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
        weatherRepository.findWeather(
                OpenWeatherApiRequestBuilder()
                        .withCityName(query)
                        .build(), openWeatherApiCallback)
        Toast.makeText(this, getString(string.searching) + " " + userInput + "...", Toast.LENGTH_SHORT)
                .show()
        updateCityNameForSwipeToRefresh(userInput)
    }

    override fun searchByLocation(lat: Double, lon: Double) {
        toggleProgressIndicator()
        val weatherRepository = ServiceConfig.getInstance()
                .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
        val cityName = cityNameFromLatLon(lat, lon)
        if (cityName == null) {
            weatherRepository.findWeather(
                    OpenWeatherApiRequestBuilder().withLatLon(lat, lon).build(),
                    openWeatherApiCallback)
            trackOnActionEvent(
                    AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH,
                            "Geocoder Failure"))
        } else {
            weatherRepository.findWeather(
                    OpenWeatherApiRequestBuilder().withCityName(cityName).build(),
                    openWeatherApiCallback)
            trackOnActionEvent(
                    AnalyticsEvent(
                            AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH, cityName))
            updateCityNameForSwipeToRefresh(cityName)
        }
    }

    private fun updateCityNameForSwipeToRefresh(cityName: CharSequence) {
        cityNameToSearchOnSwipe = cityName
    }

    private fun cityNameFromLatLon(lat: Double, lon: Double): String? {
        var cityName: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1)
            cityName = addresses[0]
                    .locality
        } catch (e: Exception) {
            Log.d(TAG, "error retrieving the cityName with Geocoder")
        }
        return cityName
    }

    private fun saveForecastList(weatherInfoList: List<WeatherInfo>) {
        executor.execute {
            val prefs = getSharedPreferences(OFFLINE_FILE, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(FORECASTS, Gson().toJson(weatherInfoList))
            editor.apply()
        }
    }

    private fun setupRecyclerView() {
        adapter = ModelAdapter(ArrayList(), this)
        adapter?.setOnItemClickListener(this)
        recyclerView = findViewById<View>(id.main_recycler_view) as RecyclerView
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
    }

    private fun notifyAdapter(weatherInfoList: List<WeatherInfo>) {
        saveForecastList(weatherInfoList)
        runOnUiThread(createNotifyRunnable(weatherInfoList))
    }

    @VisibleForTesting
    fun createNotifyRunnable(weatherInfoList: List<WeatherInfo>?): Runnable {
        return Runnable {
            adapter?.let {
                it.setItems(weatherInfoList)
                it.notifyDataSetChanged()
            }
            onItemsLoadComplete()
        }
    }

    @VisibleForTesting
    fun createSwipeToRefreshListener(): OnRefreshListener {
        return OnRefreshListener { searchByManualInput(cityNameToSearchOnSwipe) }
    }

    @VisibleForTesting
    fun createRunnableToggleProgressIndicator(): Runnable {
        return Runnable { toggleProgressIndicator() }
    }

    private fun enableSwipeToRefreshLayout() {
        swipeToRefreshLayout?.isEnabled = cityNameToSearchOnSwipe != null
    }

    companion object {
        private val TAG = WeatherListActivity::class.java.simpleName
        const val CITY_NAME_TO_SEARCH_ON_SWIPE = "mCityNameToSearchOnSwipe"
    }
}