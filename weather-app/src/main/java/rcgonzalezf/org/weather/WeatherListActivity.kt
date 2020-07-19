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
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.common.models.WeatherViewModel
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import rcgonzalezf.org.weather.R.string
import rcgonzalezf.org.weather.adapters.ModelAdapter
import rcgonzalezf.org.weather.adapters.ModelAdapter.OnItemClickListener
import rcgonzalezf.org.weather.common.BaseActivity
import rcgonzalezf.org.weather.common.ProgressIndicationStateChanger
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.databinding.WeatherListBinding
import rcgonzalezf.org.weather.list.WeatherListViewModel
import rcgonzalezf.org.weather.list.WeatherListViewModelFactory
import rcgonzalezf.org.weather.location.LocationLifecycleObserver
import rcgonzalezf.org.weather.location.LocationManager
import java.util.Locale

class WeatherListActivity : BaseActivity(),
        OnItemClickListener<WeatherViewModel>, ProgressIndicationStateChanger,
        OnUpdateWeatherListListener {

    private lateinit var adapter: ModelAdapter<WeatherInfo>
    private lateinit var locationManager: LocationManager
    private var openWeatherApiCallback: OpenWeatherApiCallback = OpenWeatherApiCallback(this)
    private lateinit var weatherListBinding: WeatherListBinding
    private val weatherListViewModel: WeatherListViewModel by viewModels {
        val geoCoder = Geocoder(this, Locale.getDefault())
        WeatherListViewModelFactory(openWeatherApiCallback,
                geoCoder, this, WeatherApp.getAppInstance())
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
        locationManager = LocationManager(this, weatherLocationSearch ,content)
        val locationLifecycleObserver = LocationLifecycleObserver(locationManager)
        lifecycle.addObserver(locationLifecycleObserver)

        // off line
        weatherListViewModel.offline.observe(this, Observer {
            val weatherInfoList = weatherListViewModel.weatherInfoList.value
            val noNetwork = AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH
            if (weatherInfoList != null && weatherInfoList.isNotEmpty()) {
                notifyAdapter(weatherInfoList)
                analyticsLifecycleObserver.trackOnActionEvent(
                        AnalyticsEvent(noNetwork, weatherInfoList[0].cityName))
            } else {
                Log.d(TAG, "No data even in offline mode :(")
                analyticsLifecycleObserver.trackOnActionEvent(AnalyticsEvent(noNetwork, "EMPTY"))
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
        toggleProgressIndicator()
        enableSwipeToRefreshLayout()
        if (weatherBinding.swipeToRefreshLayout.isRefreshing) {
            weatherBinding.swipeToRefreshLayout.isRefreshing = false
        }
    }

    @VisibleForTesting
    override fun toggleProgressIndicator() {
        val progressBar = weatherBinding.progressBar
        progressBar.visibility = if (progressBar.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
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

    private fun setupRecyclerView() {
        adapter = ModelAdapter(ArrayList())
        adapter.setOnItemClickListener(this)
        val recyclerView = weatherListBinding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun notifyAdapter(weatherInfoList: List<WeatherInfo>) {
        weatherListViewModel.saveForecastList(weatherInfoList)
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
        return OnRefreshListener {
            weatherListViewModel
                    .searchByManualInput(weatherListViewModel.cityNameToSearchOnSwipe.value ?: "")
        }
    }

    @VisibleForTesting
    fun createRunnableToggleProgressIndicator(): Runnable {
        return Runnable { toggleProgressIndicator() }
    }

    private fun enableSwipeToRefreshLayout() {
        weatherBinding.swipeToRefreshLayout.isEnabled =
                weatherListViewModel.cityNameToSearchOnSwipe.value != null
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
            weatherListViewModel.searchByManualInput(userInput)
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
}
