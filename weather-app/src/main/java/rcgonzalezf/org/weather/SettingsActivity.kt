package rcgonzalezf.org.weather

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.view.MenuItem
import androidx.core.app.NavUtils
import dagger.hilt.android.AndroidEntryPoint
import rcgonzalezf.org.weather.analytics.analytics.Analytics
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent
import rcgonzalezf.org.weather.utils.WeatherUtils.isXLargeTablet

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatPreferenceActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /** {@inheritDoc}  */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /** {@inheritDoc}  */
    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    public override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class GeneralPreferenceFragment : PreferenceFragment(), OnPreferenceChangeListener, OnPreferenceClickListener {
        private lateinit var temperatureUnitsPreference: SwitchPreference
        private lateinit var usernameToDisplay: Preference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/WeatherList/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            usernameToDisplay = findPreference(USER_NAME_TO_DISPLAY)
            bindPreferenceSummaryToValue(usernameToDisplay)
            temperatureUnitsPreference = findPreference(PREF_TEMPERATURE_UNITS) as SwitchPreference
            temperatureUnitsPreference.onPreferenceChangeListener = this
            usernameToDisplay.setOnPreferenceClickListener(this)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            val newBooleanValue = newValue == java.lang.Boolean.TRUE
            val fromValue = !newBooleanValue
            temperatureUnitsPreference.isChecked = newBooleanValue
            rcgonzalezf.org.weather.analytics.analytics.Analytics().trackOnActionEvent(
                rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                    rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.SettingsActivity.TEMP_UNITS_TOGGLE,
                    fromValue.toString()
                )
            )
            return newBooleanValue
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            rcgonzalezf.org.weather.analytics.analytics.Analytics().trackOnActionEvent(
                rcgonzalezf.org.weather.analytics.analytics.AnalyticsEvent(
                    rcgonzalezf.org.weather.analytics.analytics.AnalyticsDataCatalog.SettingsActivity.ON_NAME,
                    null
                )
            )
            return false
        }
    }

    companion object {
        const val USER_NAME_TO_DISPLAY = "user_name_to_display"
        const val PREF_TEMPERATURE_UNITS = "pref_temperature_units"

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()
            preference.summary = stringValue
            true
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference?) {
            // Set the listener to watch for value changes.
            preference!!.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            val previousValue = PreferenceManager.getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, previousValue)
        }
    }
}
