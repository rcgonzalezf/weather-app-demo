package rcgonzalezf.org.weather;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import android.view.MenuItem;
import java.util.List;
import rcgonzalezf.org.weather.common.analytics.Analytics;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;
import rcgonzalezf.org.weather.utils.WeatherUtils;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.ON_NAME;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.TEMP_UNITS_TOGGLE;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

  public static final String USER_NAME_TO_DISPLAY = "user_name_to_display";
  public static final String PREF_TEMPERATURE_UNITS = "pref_temperature_units";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupActionBar();
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      // Show the Up button in the action bar.
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override public boolean onMenuItemSelected(int featureId, MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      if (!super.onMenuItemSelected(featureId, item)) {
        NavUtils.navigateUpFromSameTask(this);
      }
      return true;
    }
    return super.onMenuItemSelected(featureId, item);
  }

  /** {@inheritDoc} */
  @Override public boolean onIsMultiPane() {
    return WeatherUtils.isXLargeTablet(this);
  }

  /** {@inheritDoc} */
  @Override public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.pref_headers, target);
  }

  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value.
   */
  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
      new Preference.OnPreferenceChangeListener() {
        @Override public boolean onPreferenceChange(Preference preference, Object value) {
          String stringValue = value.toString();
          preference.setSummary(stringValue);
          return true;
        }
      };

  /**
   * Binds a preference's summary to its value. More specifically, when the
   * preference's value is changed, its summary (line of text below the
   * preference title) is updated to reflect the value. The summary is also
   * immediately updated upon calling this method. The exact display format is
   * dependent on the type of preference.
   *
   * @see #sBindPreferenceSummaryToValueListener
   */
  private static void bindPreferenceSummaryToValue(Preference preference) {
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

    // Trigger the listener immediately with the preference's
    // current value.
    String previousValue = PreferenceManager.getDefaultSharedPreferences(preference.getContext())
        .getString(preference.getKey(), "");
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, previousValue);
  }

  /**
   * This method stops fragment injection in malicious applications.
   * Make sure to deny any unknown fragments here.
   */
  protected boolean isValidFragment(String fragmentName) {
    return PreferenceFragment.class.getName().equals(fragmentName)
        || GeneralPreferenceFragment.class.getName().equals(fragmentName);
  }

  /**
   * This fragment shows general preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  public static class GeneralPreferenceFragment extends PreferenceFragment
      implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private SwitchPreference mTemperatureUnitsPreference;
    private Preference mUsernameToDisplay;

    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_general);
      setHasOptionsMenu(true);

      // Bind the summaries of EditText/WeatherList/Dialog/Ringtone preferences
      // to their values. When their values change, their summaries are
      // updated to reflect the new value, per the Android Design
      // guidelines.
      mUsernameToDisplay = findPreference(USER_NAME_TO_DISPLAY);
      bindPreferenceSummaryToValue(mUsernameToDisplay);

      mTemperatureUnitsPreference = (SwitchPreference) findPreference(PREF_TEMPERATURE_UNITS);
      mTemperatureUnitsPreference.setOnPreferenceChangeListener(this);
      mUsernameToDisplay.setOnPreferenceClickListener(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
      boolean newBooleanValue = newValue.equals(Boolean.TRUE);
      Boolean fromValue = !newBooleanValue;
      mTemperatureUnitsPreference.setChecked(newBooleanValue);

      new Analytics().trackOnActionEvent(
          new AnalyticsEvent(TEMP_UNITS_TOGGLE, fromValue.toString()));
      return newBooleanValue;
    }

    @Override public boolean onPreferenceClick(Preference preference) {
      new Analytics().trackOnActionEvent(new AnalyticsEvent(ON_NAME, null));
      return false;
    }
  }
}
