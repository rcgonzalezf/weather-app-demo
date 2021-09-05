package rcgonzalezf.org.weather

import android.content.DialogInterface
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.common.models.WeatherViewModel
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import rcgonzalezf.org.weather.R.string
import rcgonzalezf.org.weather.adapters.ModelAdapter
import rcgonzalezf.org.weather.adapters.ModelAdapter.OnItemClickListener
import rcgonzalezf.org.weather.common.BaseActivity
import rcgonzalezf.org.weather.common.ToggleBehavior
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.common.ext.toggleVisibility
import rcgonzalezf.org.weather.databinding.WeatherListBinding
import rcgonzalezf.org.weather.list.WeatherListViewModel
import rcgonzalezf.org.weather.list.WeatherListViewModelFactory
import rcgonzalezf.org.weather.location.CityFromLatLongRetriever
import rcgonzalezf.org.weather.location.GeoCodeRetriever
import rcgonzalezf.org.weather.location.LocationLifecycleObserver
import rcgonzalezf.org.weather.location.LocationManager
import rcgonzalezf.org.weather.utils.ToastUserNotifier
import java.util.Locale

@AndroidEntryPoint
class WeatherListActivity : BaseActivity(),
        OnItemClickListener<WeatherViewModel>, ToggleBehavior, OnUpdateWeatherListListener {

    private lateinit var adapter: ModelAdapter<WeatherInfo>
    private lateinit var locationManager: LocationManager
    private var openWeatherApiCallback: OpenWeatherApiCallback = OpenWeatherApiCallback(this)

    @VisibleForTesting
    lateinit var weatherListBinding: WeatherListBinding
    private val weatherListViewModel: WeatherListViewModel by viewModels {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val cityFromLatLongRetriever: CityFromLatLongRetriever = GeoCodeRetriever(geoCoder)
        val userNotifier = ToastUserNotifier(this)
        WeatherListViewModelFactory(openWeatherApiCallback,
                cityFromLatLongRetriever, this, WeatherApp.getAppInstance(), userNotifier)
    }

    companion object {
        private val TAG = WeatherListActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherListBinding = DataBindingUtil.inflate(layoutInflater, R.layout.weather_list,
                weatherBinding.content, true)
        setupRecyclerView()
        enableSwipeToRefreshLayout()
        weatherBinding.swipeToRefreshLayout.setOnRefreshListener(createSwipeToRefreshListener())
        weatherBinding.mainFab.setOnClickListener(fabClickListener)
        val weatherLocationSearch =
                weatherListViewModel.WeatherListLocationSearch(analyticsLifecycleObserver)
        locationManager = LocationManager(this, weatherLocationSearch, content)
        val locationLifecycleObserver = LocationLifecycleObserver(locationManager)
        lifecycle.addObserver(locationLifecycleObserver)

        // off line
        weatherListViewModel.offline.observe(this, Observer {
            val weatherInfoList = weatherListViewModel.weatherInfoList.value
            val noNetwork = rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH
            if (weatherInfoList != null && weatherInfoList.isNotEmpty()) {
                notifyAdapter(weatherInfoList)
                analyticsLifecycleObserver.trackOnActionEvent(
                    rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                        noNetwork,
                        weatherInfoList[0].cityName
                    )
                )
            } else {
                Log.d(TAG, "No data even in offline mode :(")
                analyticsLifecycleObserver.trackOnActionEvent(
                    rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                        noNetwork,
                        "EMPTY"
                    )
                )
                //cancel swipe to refresh loading
                onItemsLoadComplete()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @VisibleForTesting
    fun onItemsLoadComplete() {
        toggle()
        enableSwipeToRefreshLayout()
        if (weatherBinding.swipeToRefreshLayout.isRefreshing) {
            weatherBinding.swipeToRefreshLayout.isRefreshing = false
        }
    }

    @VisibleForTesting
    override fun toggle() {
        weatherBinding.progressBar.toggleVisibility()
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        weatherListBinding.mainRecyclerView.scheduleLayoutAnimation()
    }

    override fun onItemClick(view: View, viewModel: WeatherViewModel) {
        Toast.makeText(this,
                String.format(
                        getString(string.item_clicked_debug_format), viewModel.id,
                        viewModel.dateTime, viewModel.description),
                Toast.LENGTH_SHORT)
                .show()
    }

    override fun updateList(weatherInfoList: List<WeatherInfo>) {
        val cityName = if (weatherInfoList.isEmpty()) "" else weatherInfoList[0].cityName
        analyticsLifecycleObserver.trackOnActionEvent(
            rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED,
                "cityName: $cityName"
            )
        )
        notifyAdapter(weatherInfoList)
    }

    override fun onError(error: String) {
        // TODO implement error handling
        runOnUiThread { toggle() }
        Log.d(TAG, error)
        analyticsLifecycleObserver.trackOnActionEvent(
            rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED,
                "error: $error"
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = ModelAdapter(ArrayList())
        adapter.setOnItemClickListener(this)
        val recyclerView = weatherListBinding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun notifyAdapter(weatherInfoList: List<WeatherInfo>) {
        weatherListViewModel.saveForecastList(weatherInfoList)
        runOnUiThread {
            adapter.setItems(weatherInfoList)

            onItemsLoadComplete()
        }
    }

    @VisibleForTesting
    fun createSwipeToRefreshListener(): OnRefreshListener {
        return OnRefreshListener {
            weatherListViewModel
                    .searchByManualInput(weatherListViewModel.cityNameToSearchOnSwipe.value ?: "")
        }
    }

    private fun enableSwipeToRefreshLayout() {
        weatherBinding.swipeToRefreshLayout.isEnabled =
                weatherListViewModel.cityNameToSearchOnSwipe.value != null
    }

    @get:VisibleForTesting
    val fabClickListener: View.OnClickListener
        get() = View.OnClickListener {
            val promptsView = View.inflate(this, R.layout.dialog_city_query, null)
            val userInput = promptsView.findViewById<View>(R.id.city_input_edit_text) as EditText
            AlertDialog.Builder(this).setView(promptsView)
                    .setCancelable(false)
                    .setPositiveButton("OK", getOkClickListener(userInput.text))
                    .setNegativeButton("Cancel", cancelListener)
                    .create()
                    .show()
        }

    @get:VisibleForTesting
    val cancelListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { dialog, _ ->
            analyticsLifecycleObserver
                    .trackOnActionEvent(
                        rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                            rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.WeatherListActivity.MANUAL_SEARCH,
                            "CANCEL"
                        )
                    )
            dialog.cancel()
        }

    @VisibleForTesting
    fun getOkClickListener(userInput: CharSequence): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            analyticsLifecycleObserver
                    .trackOnActionEvent(
                        rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                            rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.WeatherListActivity.MANUAL_SEARCH,
                            userInput.toString()
                        )
                    )
            weatherListViewModel.searchByManualInput(userInput)
        }
    }
}
