package rcgonzalezf.org.weather.common

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.SettingsActivity
import rcgonzalezf.org.weather.common.analytics.Analytics
import rcgonzalezf.org.weather.common.analytics.AnalyticsLifecycleObserver
import rcgonzalezf.org.weather.databinding.WeatherBinding

abstract class BaseActivity : AppCompatActivity(),
        ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var drawerLayout: DrawerLayout
    protected lateinit var content: View
    protected lateinit var analyticsLifecycleObserver: AnalyticsLifecycleObserver
    protected lateinit var weatherBinding: WeatherBinding

    companion object {
        const val OFFLINE_FILE = "OFFLINE_WEATHER"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherBinding = DataBindingUtil.setContentView(this, R.layout.weather)
        //setContentView(R.layout.weather)
        initToolbar()
        setupDrawerLayout()
        content = weatherBinding.content
        analyticsLifecycleObserver = AnalyticsLifecycleObserver(this.javaClass.simpleName,
                Analytics())
        lifecycle.addObserver(analyticsLifecycleObserver)
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
        val toolbar = weatherBinding.toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    fun setupDrawerLayout() {
        drawerLayout = weatherBinding.drawerLayout
        val view = weatherBinding.navigationView
        view.setNavigationItemSelectedListener(navigationListener)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val textView = weatherBinding
                .drawerLayout.findViewById<View>(R.id.user_display_name) as TextView?
        textView?.text =
                prefs.getString(SettingsActivity.USER_NAME_TO_DISPLAY,
                        getString(R.string.pref_default_display_name))
    }

    @VisibleForTesting
    fun homePressed(menuItem: MenuItem) {
        Snackbar.make(content, "${menuItem.title} pressed", Snackbar.LENGTH_SHORT).show()
        menuItem.isChecked = true
        drawerLayout.closeDrawers()
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
}
