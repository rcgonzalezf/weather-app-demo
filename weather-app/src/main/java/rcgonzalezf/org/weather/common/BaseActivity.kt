package rcgonzalezf.org.weather.common

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.rcgonzalezf.weather.common.models.WeatherInfo
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.SettingsActivity
import rcgonzalezf.org.weather.common.analytics.Analytics
import rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.Companion.MANUAL_SEARCH
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.location.LocationManager
import rcgonzalezf.org.weather.utils.WeatherUtils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

abstract class BaseActivity : AppCompatActivity(),
        ActivityCompat.OnRequestPermissionsResultCallback, OnOfflineLoader {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var content: View
    private lateinit var locationManager: LocationManager

    companion object {
        const val OFFLINE_FILE = "OFFLINE_WEATHER"
        const val FORECASTS = "FORECASTS"
        private val TAG = BaseActivity::class.java.simpleName
    }

    protected abstract fun searchByQuery(query: String, userInput: CharSequence)
    abstract fun searchByLocation(lat: Double, lon: Double)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)
        initToolbar()
        setupDrawerLayout()
        findViewById<View>(R.id.main_fab).setOnClickListener(fabClickListener)
        content = findViewById(R.id.content)
        locationManager = LocationManager(this, content)
        trackOnScreen()
    }

    public override fun onStart() {
        super.onStart()
        locationManager.connect()
    }

    public override fun onStop() {
        locationManager.disconnect()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_settings -> {
                navigateToSettings()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    fun setupDrawerLayout() {
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val view = findViewById<View>(R.id.navigation_view) as NavigationView
        view.setNavigationItemSelectedListener(navigationListener)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val textView = findViewById<View>(R.id.user_display_name) as TextView?
        textView?.text =
                prefs.getString(SettingsActivity.USER_NAME_TO_DISPLAY,
                        getString(R.string.pref_default_display_name))
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

    fun informNoInternet() {
        Toast.makeText(this, getString(R.string.no_internet_msg),
                Toast.LENGTH_SHORT).show()
        val weatherInfoList = previousForecastList
        loadOldData(weatherInfoList)
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @VisibleForTesting
    fun searchByManualInput(userInput: CharSequence) {
        val query: String
        query = try {
            URLEncoder.encode(userInput.toString(), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Can't encode URL", e)
            Toast.makeText(this@BaseActivity, "${getString(R.string.invalid_input)}: $userInput...",
                    Toast.LENGTH_SHORT).show()
            return
        }
        if (!WeatherUtils.hasInternetConnection(this)) {
            informNoInternet()
        } else {
            searchByQuery(query, userInput)
        }
    }

    @VisibleForTesting
    fun homePressed(menuItem: MenuItem) {
        Snackbar.make(content, "${menuItem.title} pressed", Snackbar.LENGTH_SHORT).show()
        menuItem.isChecked = true
        drawerLayout.closeDrawers()
    }

    @get:VisibleForTesting
    val fabClickListener: View.OnClickListener
        get() = View.OnClickListener { performFabAction() }

    @get:VisibleForTesting
    val cancelListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { dialog, id ->
            trackOnActionEvent(AnalyticsEvent(MANUAL_SEARCH, "CANCEL"))
            dialog.cancel()
        }

    @VisibleForTesting
    fun getOkClickListener(
            userInput: CharSequence): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialog, id ->
            trackOnActionEvent(AnalyticsEvent(MANUAL_SEARCH, userInput.toString()))
            searchByManualInput(userInput)
        }
    }

    @get:VisibleForTesting
    val navigationListener: NavigationView.OnNavigationItemSelectedListener
        get() = NavigationView.OnNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.drawer_settings) {
                navigateToSettings()
            } else {
                homePressed(menuItem)
            }
            true
        }

    private fun navigateToSettings() {
        val intent = Intent(this@BaseActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun trackOnScreen() {
        Analytics().trackOnScreen(this.javaClass.simpleName)
    }

    fun trackOnActionEvent(event: AnalyticsEvent) {
        Analytics().trackOnActionEvent(event)
    }
}
