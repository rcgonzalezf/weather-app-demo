package rcgonzalezf.org.weather.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.common.models.Forecast;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.location.LocationManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static rcgonzalezf.org.weather.common.BaseActivity.FORECASTS;

@RunWith(JMockit.class) public class BaseActivityTest {

  @Tested private BaseActivity uut;

  @SuppressWarnings("unused") @Mocked private FragmentActivity mFragmentActivity;
  @SuppressWarnings("unused") @Mocked private AppCompatDelegate mDelegate;

  @SuppressWarnings("unused") @Mocked private Toolbar mToolbar;
  @SuppressWarnings("unused") @Mocked private DrawerLayout mDrawerLayout;
  @SuppressWarnings("unused") @Mocked private NavigationView mNavigationView;
  @SuppressWarnings("unused") @Mocked private TextView mTextView;
  @SuppressWarnings("unused") @Mocked private EditText mEditText;
  @SuppressWarnings("unused") @Mocked private SharedPreferences mSharedPreferences;
  @SuppressWarnings("unused") @Mocked private PreferenceManager mPreferenceManager;
  @SuppressWarnings("unused") @Mocked private FloatingActionButton mFloatingActionButton;

  private boolean mRetrievingFromCache;
  private boolean mSearchingByLocation;
  private boolean mSearchingByQuery;
  private boolean mSuperOnOptionsItemSelectedCalled;
  private boolean mIsNullUserDisplayName;
  private View mView;
  private String mStoredData;

  @Before public void setUp() {
    mView = new MockUp<View>() {
      @Mock View inflate(Context context, int resource, ViewGroup root) {
        return mEditText;
      }

      @Mock View findViewById(int id) {
        return mEditText;
      }
    }.getMockInstance();
    new MockUp<AppCompatActivity>() {
      @SuppressWarnings("unused") @Mock View findViewById(@IdRes int id) {
        View view = mView;
        if (id == R.id.toolbar) {
          view = mToolbar;
        } else if (id == R.id.drawer_layout) {
          view = mDrawerLayout;
        } else if (id == R.id.navigation_view) {
          view = mNavigationView;
        } else if (id == R.id.user_display_name) {
          view = mIsNullUserDisplayName ? null : mTextView;
        } else if (id == R.id.main_fab) {
          view = mFloatingActionButton;
        }
        return view;
      }

      @SuppressWarnings("unused") @Mock boolean onOptionsItemSelected(MenuItem item) {
        mSuperOnOptionsItemSelectedCalled = true;
        return mSuperOnOptionsItemSelectedCalled;
      }
    };

    uut = new BaseActivity() {
      @Override protected void searchByQuery(String query, Editable userInput) {
        mSearchingByQuery = true;
      }

      @Override public void searchByLocation(double lat, double lon) {
        mSearchingByLocation = true;
      }

      @Override public void loadOldData(List<Forecast> forecastList) {
        mRetrievingFromCache = true;
      }
    };
  }

  @Test public void shouldInitLocationManagerWhenCreatingTheActivity(
      @SuppressWarnings("UnusedParameters") @Mocked LocationManager mLocationManager) {

    whenCreatingTheActivity();

    thenLocationManagerShouldBeInstantiated();
  }

  @Test public void shouldConnectLocationManagerOnStart(@Mocked LocationManager mLocationManager) {
    givenActivityCreated();

    whenStartingTheActivity();

    thenLocationManagerShouldBeConnected(mLocationManager);
  }

  @Test
  public void shouldDisconnectLocationManagerOnStop(@Mocked LocationManager mLocationManager) {
    givenActivityCreated();

    whenStoppingTheActivity();

    thenLocationManagerShouldBeDisconnected(mLocationManager);
  }

  @Test public void shouldCreateOptionsMenu() {
    boolean createOptionsMenu = whenCreatingOptionsMenu();

    thenMenuShouldBeCreated(createOptionsMenu);
  }

  @Test public void shouldOpenDrawerWhenSelectingOptionMenu(@Mocked MenuItem item) {
    givenActivityCreated();
    givenMenuItemId(item, android.R.id.home);

    whenOptionItemIsSelected(item);

    thenShouldOpenDrawer();
    thenShouldCallSuperOnOptionsItemSelected(false);
  }

  @Test public void shouldNavigateToSettingWhenSelectingOptionMenu(@Mocked MenuItem item,
      @Mocked Intent intent) {
    givenActivityCreated();
    givenMenuItemId(item, R.id.action_settings);

    whenOptionItemIsSelected(item);

    thenShouldNavigateToSettings(intent);
    thenShouldCallSuperOnOptionsItemSelected(false);
  }

  @Test
  public void shouldCallSuperWhenSelectingOptionMenuAndNoItemIsExpected(@Mocked MenuItem item) {
    givenActivityCreated();
    givenMenuItemId(item, -1);

    whenOptionItemIsSelected(item);

    thenShouldCallSuperOnOptionsItemSelected(true);
  }

  @Test public void shouldNotInteractOnNullActionBar(@Mocked ActionBar actionBar) {
    givenActivityCreated();
    givenNullActionBar();

    whenInitializingTheToolBar();

    thenNoActionBarInteractions(actionBar);
  }

  @Test public void shouldNotInteractOnNullUserDisplayNameTextView() {
    givenNullUserDisplayName();
    givenActivityCreated();

    whenSettingUpTheDrawer();

    thenNoTextViewInteractions(mTextView);
  }

  @Test
  public void shouldShowAlertDialogWhenPressingFabForManualInput(@Mocked AlertDialog alertDialog,
      @SuppressWarnings("UnusedParameters") @Mocked AlertDialog.Builder alertDialogBuilder) {
    givenActivityCreated();

    whenPerformingFabAction();

    thenAlertDialogShouldBeShown(alertDialog);
  }

  @Test public void shouldInformNoInternet(@Mocked Toast toast) {
    whenInformingNoInternet();

    thenShouldShowTheToast(toast);
  }

  @Test public void shouldTryToLoadPreviousDataWhenNoInternet(
      @SuppressWarnings("UnusedParameters") @Mocked Toast toast) {
    whenInformingNoInternet();

    thenShouldLoadData();
  }

  @Test public void shouldGetSavedStoredData() {
    givenEmptyForecastListStoredData();
    givenSharedPreferenceWithStoredData();

    List<Forecast> storedData = whenGettingTheSavedData();

    thenShouldHaveStoredData(storedData);
  }

  private void thenShouldHaveStoredData(List<Forecast> storedData) {
    assertNotNull(storedData);
  }

  private List<Forecast> whenGettingTheSavedData() {
    return uut.getPreviousForecastList();
  }

  private void givenSharedPreferenceWithStoredData() {
    new Expectations() {{
      mSharedPreferences.getString(FORECASTS, null);
      result = mStoredData;
    }};
  }

  private void givenEmptyForecastListStoredData() {
    List<Forecast> dataToStore = new ArrayList<>();
    dataToStore.add(new Forecast());
    mStoredData = new Gson().toJson(dataToStore);
  }

  private void thenShouldLoadData() {
    assertTrue(mRetrievingFromCache);
  }

  private void thenShouldShowTheToast(final Toast toast) {
    new Verifications() {{
      toast.show();
    }};
  }

  private void whenInformingNoInternet() {
    uut.informNoInternet();
  }

  private void thenAlertDialogShouldBeShown(final AlertDialog alertDialog) {
    new Verifications() {{
      alertDialog.show();
    }};
  }

  private void whenPerformingFabAction() {
    uut.performFabAction();
  }

  private void givenNullUserDisplayName() {
    mIsNullUserDisplayName = true;
  }

  private void whenSettingUpTheDrawer() {
    uut.setupDrawerLayout();
  }

  private void thenNoTextViewInteractions(TextView mTextView) {
    new FullVerifications(mTextView) {
    };
  }

  private void thenNoActionBarInteractions(final ActionBar actionBar) {
    new FullVerifications(actionBar) {
    };
  }

  private void whenInitializingTheToolBar() {
    uut.initToolbar();
  }

  private void givenNullActionBar() {
    new Expectations() {{
      mDelegate.getSupportActionBar();
      result = null;
    }};
  }

  private void thenShouldCallSuperOnOptionsItemSelected(boolean expected) {
    assertEquals(expected, mSuperOnOptionsItemSelectedCalled);
  }

  private void thenShouldNavigateToSettings(final Intent intent) {
    new Verifications() {{
      new Intent(uut, SettingsActivity.class);
      uut.startActivity(withAny(intent));
    }};
  }

  private void thenShouldOpenDrawer() {
    new Verifications() {{
      mDrawerLayout.openDrawer(GravityCompat.START);
    }};
  }

  private void whenOptionItemIsSelected(MenuItem item) {
    uut.onOptionsItemSelected(item);
  }

  private void givenMenuItemId(final MenuItem item, final int itemId) {
    new Expectations() {{
      item.getItemId();
      result = itemId;
    }};
  }

  private void thenMenuShouldBeCreated(boolean createOptionsMenu) {
    assertTrue(createOptionsMenu);
  }

  private boolean whenCreatingOptionsMenu() {
    return uut.onCreateOptionsMenu(mock(Menu.class));
  }

  private void thenLocationManagerShouldBeDisconnected(final LocationManager mLocationManager) {
    new Verifications() {{
      mLocationManager.disconnect();
    }};
  }

  private void whenStoppingTheActivity() {
    uut.onStop();
  }

  private void givenActivityCreated() {
    uut.onCreate(null);
  }

  private void thenLocationManagerShouldBeConnected(final LocationManager mLocationManager) {
    new Verifications() {{
      mLocationManager.connect();
    }};
  }

  private void whenStartingTheActivity() {
    uut.onStart();
  }

  private void thenLocationManagerShouldBeInstantiated() {
    new Verifications() {{
      new LocationManager(uut, withAny(mView));
    }};
  }

  private void whenCreatingTheActivity() {
    uut.onCreate(null);
  }
}
