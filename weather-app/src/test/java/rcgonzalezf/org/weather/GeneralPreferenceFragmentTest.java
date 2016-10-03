package rcgonzalezf.org.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static rcgonzalezf.org.weather.SettingsActivity.USER_NAME_TO_DISPLAY;

@RunWith(JMockit.class) public class GeneralPreferenceFragmentTest {

  @Tested private SettingsActivity.GeneralPreferenceFragment uut;

  @SuppressWarnings("unused") @Mocked private PreferenceFragment mPreferenceFragment;
  @SuppressWarnings("unused") @Mocked private Context mContext;
  @SuppressWarnings("unused") @Mocked private SharedPreferences mSharedPreferences;
  @SuppressWarnings("unused") @Mocked private PreferenceManager mPreferenceManager;
  private boolean mOptionsItemSelected;

  @Before public void setUp() throws Exception {
    uut = new SettingsActivity.GeneralPreferenceFragment();
  }

  @Test public void shouldFindUserNameToDisplayPreferenceOnCreating() {
    givenPreviousValue();

    whenCreatingGeneralPreferenceFragment();

    thenShouldBindPreference(USER_NAME_TO_DISPLAY);
  }

  @Test public void shouldStartActivityOnPressingHome(@Mocked MenuItem menuItem) {
    givenMenuItemPressedIsHome(menuItem);

    whenHandlingOptionsItemSelected(menuItem);

    thenShouldStartActivity();
    thenShouldHandleOnOptionsItemSelected();
  }

  @Test public void shouldDelegateOptionsItemSelected(@Mocked MenuItem menuItem) {
    whenHandlingOptionsItemSelected(menuItem);

    thenShouldDelegateOptionsItemSelected(menuItem);
  }

  private void thenShouldDelegateOptionsItemSelected(final MenuItem menuItem) {
    new Verifications() {{
      mPreferenceFragment.onOptionsItemSelected(menuItem);
    }};
  }

  private void thenShouldHandleOnOptionsItemSelected() {
    assertTrue(mOptionsItemSelected);
  }

  private void thenShouldStartActivity() {
    new Verifications() {{
      mPreferenceFragment.startActivity(withAny(new Intent()));
    }};
  }

  private void whenHandlingOptionsItemSelected(MenuItem menuItem) {
    mOptionsItemSelected = uut.onOptionsItemSelected(menuItem);
  }

  private void givenMenuItemPressedIsHome(final MenuItem menuItem) {
    new Expectations() {{
      menuItem.getItemId(); result = android.R.id.home;
    }};
  }

  private void givenPreviousValue() {
    new Expectations() {{
      mSharedPreferences.getString(withAny("key"), "");
      result = "someUserName";
    }};
  }

  private void thenShouldBindPreference(final String preferenceName) {
    new Verifications() {{
      mPreferenceFragment.findPreference(preferenceName);
    }};
  }

  private void whenCreatingGeneralPreferenceFragment() {
    uut.onCreate(new Bundle());
  }
}