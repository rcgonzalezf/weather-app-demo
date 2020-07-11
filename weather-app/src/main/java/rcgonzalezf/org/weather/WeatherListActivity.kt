package rcgonzalezf.org.weather

import android.content.Context
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import rcgonzalezf.org.weather.common.analytics.AnalyticsLifecycleObserver
import rcgonzalezf.org.weather.databinding.WeatherListBinding
import rcgonzalezf.org.weather.location.LocationLifecycleObserver
import rcgonzalezf.org.weather.location.LocationManager
import rcgonzalezf.org.weather.location.LocationSearch
import rcgonzalezf.org.weather.utils.WeatherUtils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class WeatherListActivity : BaseActivity(), OnItemClickListener<WeatherViewModel>,
        OnUpdateWeatherListListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: ModelAdapter<WeatherInfo>
    private lateinit var locationManager: LocationManager
    private var openWeatherApiCallback: OpenWeatherApiCallback? = null
    private var cityNameToSearchOnSwipe: CharSequence? = null
    private lateinit var progress: ProgressBar
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private lateinit var weatherListBinding: WeatherListBinding

    companion object {
        private val TAG = WeatherListActivity::class.java.simpleName
        const val FORECASTS = "FORECASTS"
        const val CITY_NAME_TO_SEARCH_ON_SWIPE = "mCityNameToSearchOnSwipe"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherListBinding = DataBindingUtil.inflate(layoutInflater, R.layout.weather_list,
                weatherBinding.content, true)
        openWeatherApiCallback = OpenWeatherApiCallback(this)
        setupRecyclerView()
        cityNameToSearchOnSwipe = savedInstanceState?.getCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE)
        progress = weatherBinding.progressBar
        swipeToRefreshLayout = weatherBinding.swipeToRefreshLayout
        enableSwipeToRefreshLayout()
        swipeToRefreshLayout.setOnRefreshListener(createSwipeToRefreshListener())
        weatherBinding.mainFab.setOnClickListener(fabClickListener)
        val weatherLocationSearch = WeatherListLocationSearch(analyticsLifecycleObserver)
        locationManager = LocationManager(this, weatherLocationSearch ,content)
        val locationLifecycleObserver = LocationLifecycleObserver(locationManager)
        lifecycle.addObserver(locationLifecycleObserver)
    }

    override fun informNoInternet() {
        Toast.makeText(this, getString(R.string.no_internet_msg),
                Toast.LENGTH_SHORT).show()
        val weatherInfoList = previousForecastList
        loadOldData(weatherInfoList)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE, cityNameToSearchOnSwipe)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @VisibleForTesting
    fun onItemsLoadComplete() {
        toggleProgressIndicator()
        enableSwipeToRefreshLayout()
        if (swipeToRefreshLayout.isRefreshing) {
            swipeToRefreshLayout.isRefreshing = false
        }
    }

    @VisibleForTesting
    fun toggleProgressIndicator() {
        progress.visibility = if (progress.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        recyclerView.scheduleLayoutAnimation()
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
            analyticsLifecycleObserver.trackOnActionEvent(
                    AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH,
                            weatherInfoList[0].cityName))
        } else {
            Log.d(TAG, "No data even in offline mode :(")
            analyticsLifecycleObserver.trackOnActionEvent(
                    AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH,
                            "EMPTY"))
            //cancel swipe to refresh loading
            onItemsLoadComplete()
        }
    }

    override fun updateList(weatherInfoList: List<WeatherInfo>) {
        val cityName = if (weatherInfoList.isEmpty()) "" else weatherInfoList[0].cityName
        analyticsLifecycleObserver.trackOnActionEvent(
                AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED,
                        "cityName: $cityName"))
        notifyAdapter(weatherInfoList)
    }

    override fun onError(error: String) {
        // TODO implement error handling
        runOnUiThread(createRunnableToggleProgressIndicator())
        Log.d(TAG, error)
        analyticsLifecycleObserver.trackOnActionEvent(
                AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED,
                        "error: $error"))
    }

    fun searchByQuery(query: String, userInput: CharSequence) {
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

    private fun updateCityNameForSwipeToRefresh(cityName: CharSequence) {
        cityNameToSearchOnSwipe = cityName
    }

    private fun cityNameFromLatLon(lat: Double, lon: Double): String? {
        var cityName: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1)
            cityName = addresses[0].locality
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
        adapter = ModelAdapter(ArrayList())
        adapter.setOnItemClickListener(this)
        recyclerView = weatherListBinding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun notifyAdapter(weatherInfoList: List<WeatherInfo>) {
        saveForecastList(weatherInfoList)
        runOnUiThread(createNotifyRunnable(weatherInfoList))
    }

    @VisibleForTesting
    fun createNotifyRunnable(weatherInfoList: List<WeatherInfo>): Runnable {
        return Runnable {
            adapter.setItems(weatherInfoList)
            adapter.notifyDataSetChanged()

            onItemsLoadComplete()
        }
    }

    @VisibleForTesting
    fun createSwipeToRefreshListener(): OnRefreshListener {
        return OnRefreshListener { searchByManualInput(cityNameToSearchOnSwipe ?: "") }
    }

    @VisibleForTesting
    fun createRunnableToggleProgressIndicator(): Runnable {
        return Runnable { toggleProgressIndicator() }
    }

    private fun enableSwipeToRefreshLayout() {
        swipeToRefreshLayout.isEnabled = cityNameToSearchOnSwipe != null
    }

    val previousForecastList: List<WeatherInfo>?
        get() {
            val sharedPreferences = getSharedPreferences(OFFLINE_FILE, 0)
            val serializedData = sharedPreferences.getString(FORECASTS, null)
            var storedData: List<WeatherInfo>? = null
            if (serializedData != null) {
                storedData = Gson()
                        .fromJson(serializedData, object : TypeToken<List<WeatherInfo?>?>() {}.type)
            }
            return storedData
        }

    @get:VisibleForTesting
    val fabClickListener: View.OnClickListener
        get() = View.OnClickListener { performFabAction() }

    @get:VisibleForTesting
    val cancelListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { dialog, id ->
            analyticsLifecycleObserver
                    .trackOnActionEvent(AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.MANUAL_SEARCH, "CANCEL"))
            dialog.cancel()
        }

    @VisibleForTesting
    fun getOkClickListener(
            userInput: CharSequence): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialog, id ->
            analyticsLifecycleObserver
                    .trackOnActionEvent(AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.MANUAL_SEARCH, userInput.toString()))
            searchByManualInput(userInput)
        }
    }

    fun performFabAction() {
        val promptsView = View.inflate(this, R.layout.dialog_city_query, null)
        val userInput = promptsView.findViewById<View>(R.id.city_input_edit_text) as EditText
        AlertDialog.Builder(this).setView(promptsView)
                .setCancelable(false)
                .setPositiveButton("OK", getOkClickListener(userInput.text))
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show()
    }

    @VisibleForTesting
    fun searchByManualInput(userInput: CharSequence) {
        val query: String
        query = try {
            URLEncoder.encode(userInput.toString(), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Can't encode URL", e)
            Toast.makeText(this@WeatherListActivity, "${getString(R.string.invalid_input)}: $userInput...",
                    Toast.LENGTH_SHORT).show()
            return
        }
        if (!WeatherUtils.hasInternetConnection(this)) {
            informNoInternet()
        } else {
            searchByQuery(query, userInput)
        }
    }

    inner class WeatherListLocationSearch(val analytics: AnalyticsLifecycleObserver): LocationSearch {
        override fun searchByLatLon(lat: Double, lon: Double) {
            toggleProgressIndicator()
            val weatherRepository = ServiceConfig.getInstance()
                    .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>()
            val cityName = cityNameFromLatLon(lat, lon)
            if (cityName == null) {
                weatherRepository.findWeather(
                        OpenWeatherApiRequestBuilder().withLatLon(lat, lon).build(),
                        openWeatherApiCallback)
                analytics.trackOnActionEvent(
                        AnalyticsEvent(AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH,
                                "Geocoder Failure"))
            } else {
                weatherRepository.findWeather(
                        OpenWeatherApiRequestBuilder().withCityName(cityName).build(),
                        openWeatherApiCallback)
                analytics.trackOnActionEvent(
                        AnalyticsEvent(
                                AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH, cityName))
                updateCityNameForSwipeToRefresh(cityName)
            }
        }
    }
}
