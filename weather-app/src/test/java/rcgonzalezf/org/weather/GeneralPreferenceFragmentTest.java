package rcgonzalezf.org.weather;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.rcgonzalezf.weather.WeatherLibApp;

import rcgonzalezf.org.weather.common.analytics.Analytics;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager;

import static org.junit.Assert.assertTrue;
import static rcgonzalezf.org.weather.SettingsActivity.PREF_TEMPERATURE_UNITS;
import static rcgonzalezf.org.weather.SettingsActivity.USER_NAME_TO_DISPLAY;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.ON_NAME;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.SettingsActivity.TEMP_UNITS_TOGGLE;

@RunWith(JMockit.class) public class GeneralPreferenceFragmentTest {

  @Tested private SettingsActivity.GeneralPreferenceFragment uut;

  @SuppressWarnings("unused") @Mocked private PreferenceFragment preferenceFragment;
  @SuppressWarnings("unused") @Mocked private Context context;
  @SuppressWarnings("unused") @Mocked private SharedPreferences sharedPreferences;
  @SuppressWarnings("unused") @Mocked private PreferenceManager preferenceManager;
  @SuppressWarnings("unused") @Mocked private Preference usernameToDisplay;
  @SuppressWarnings("unused") @Mocked private SwitchPreference temperatureUnitsPreference;
  @SuppressWarnings("unused") @Mocked private AnalyticsManager analyticsManager;
  @org.mockito.Mock
  private WeatherApp weatherApp;
  private boolean mOptionsItemSelected;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    WeatherLibApp.setAppInstance(weatherApp);
    uut = new SettingsActivity.GeneralPreferenceFragment();
  }

  @Test public void shouldFindUserNameToDisplayPreferenceOnCreating() {
    givenPreferences();
    givenPreviousValue();

    whenCreatingGeneralPreferenceFragment();

    thenShouldBindPreference(USER_NAME_TO_DISPLAY);
  }

  @Test public void shouldStartActivityOnPressingHome(@Mocked MenuItem menuItem,
      @SuppressWarnings("UnusedParameters") @Mocked Fragment fragment) {
    givenMenuItemPressedIsHome(menuItem);

    whenHandlingOptionsItemSelected(menuItem);

    thenShouldStartActivity();
    thenShouldHandleOnOptionsItemSelected();
  }

  @Test public void shouldDelegateOptionsItemSelected(@Mocked MenuItem menuItem) {
    whenHandlingOptionsItemSelected(menuItem);

    thenShouldDelegateOptionsItemSelected(menuItem);
  }

  @Test public void shouldTrackPreferenceChangeFromFalse(@Mocked Analytics analytics,
      @Mocked AnalyticsEvent analyticsEvent) {
    givenTemperatureUnitPreference();

    whenPreferenceChange(Boolean.TRUE);

    thenShouldTrackEvent(TEMP_UNITS_TOGGLE, Boolean.FALSE, analytics, analyticsEvent);
  }

  @Test public void shouldTrackPreferenceChangeFromTrue(@Mocked Analytics analytics,
      @Mocked AnalyticsEvent analyticsEvent) {
    givenTemperatureUnitPreference();

    whenPreferenceChange(Boolean.FALSE);

    thenShouldTrackEvent(TEMP_UNITS_TOGGLE, Boolean.TRUE, analytics, analyticsEvent);
  }

  @Test public void shouldTrackPreferenceClick(@Mocked Analytics analytics,
      @Mocked AnalyticsEvent analyticsEvent) {
    whenPreferenceClick();

    thenShouldTrackEvent(ON_NAME, analytics, analyticsEvent);
  }

  private void givenTemperatureUnitPreference() {
    Deencapsulation.setField(uut, "temperatureUnitsPreference", temperatureUnitsPreference);
  }

  private void whenPreferenceClick() {
    uut.onPreferenceClick(usernameToDisplay);
  }

  private void thenShouldTrackEvent(final String nameChange, final Analytics analytics,
      final AnalyticsEvent analyticsEvent) {
    new Verifications() {{
      //noinspection WrongConstant
      new AnalyticsEvent(withEqual(nameChange), null);
      analytics.trackOnActionEvent(withAny(analyticsEvent));
    }};
  }

  private void thenShouldTrackEvent(final String tempUnitsToggle, final Boolean previousValue,
      final Analytics analytics, final AnalyticsEvent analyticsEvent) {
    new Verifications() {{
      //noinspection WrongConstant
      new AnalyticsEvent(withEqual(tempUnitsToggle), withEqual(previousValue.toString()));
      analytics.trackOnActionEvent(withAny(analyticsEvent));
    }};
  }

  private void whenPreferenceChange(Object newValue) {
    uut.onPreferenceChange(temperatureUnitsPreference, newValue);
  }

  private void givenPreferences() {
    new Expectations() {{
      preferenceFragment.findPreference(PREF_TEMPERATURE_UNITS);
      result = temperatureUnitsPreference;
    }};
  }

  private void thenShouldDelegateOptionsItemSelected(final MenuItem menuItem) {
    new Verifications() {{
      preferenceFragment.onOptionsItemSelected(menuItem);
    }};
  }

  private void thenShouldHandleOnOptionsItemSelected() {
    assertTrue(mOptionsItemSelected);
  }

  private void thenShouldStartActivity() {
    new Verifications() {{
      preferenceFragment.startActivity(withAny(new Intent()));
    }};
  }

  private void whenHandlingOptionsItemSelected(MenuItem menuItem) {
    mOptionsItemSelected = uut.onOptionsItemSelected(menuItem);
  }

  private void givenMenuItemPressedIsHome(final MenuItem menuItem) {
    new Expectations() {{
      menuItem.getItemId();
      result = android.R.id.home;
    }};
  }

  private void givenPreviousValue() {
    new Expectations() {{
      sharedPreferences.getString(withAny("key"), "");
      result = "someUserName";
    }};
  }

  private void thenShouldBindPreference(final String preferenceName) {
    new Verifications() {{
      preferenceFragment.findPreference(preferenceName);
    }};
  }

  private void whenCreatingGeneralPreferenceFragment() {
    uut.onCreate(new Bundle());
  }
}