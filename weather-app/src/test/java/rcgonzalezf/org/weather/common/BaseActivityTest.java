package rcgonzalezf.org.weather.common;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.common.models.Forecast;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.common.analytics.Analytics;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;
import rcgonzalezf.org.weather.location.LocationManager;
import rcgonzalezf.org.weather.utils.WeatherUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static rcgonzalezf.org.weather.common.BaseActivity.FORECASTS;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.MANUAL_SEARCH;

@RunWith(JMockit.class) public class BaseActivityTest {

  private BaseActivity uut;

  @SuppressWarnings("unused") @Mocked private Activity mActivity;
  @SuppressWarnings("unused") @Mocked private ContextWrapper mContextWrapper;
  @SuppressWarnings("unused") @Mocked private Context mContext;
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
  @SuppressWarnings("unused") @Mocked private Analytics mAnalytics;

  private boolean mRetrievingFromCache;
  private boolean mSearchingByQuery;
  private boolean mSuperOnOptionsItemSelectedCalled;
  private boolean mIsNullUserDisplayName;
  private String mStoredData;
  private int mRequestCode;
  private String[] mPermissions;
  private int[] mGrantResults;
  private CharSequence mUserInput;
  private View.OnClickListener mFabClickListener;
  private DialogInterface.OnClickListener mDialogClickListener;
  private NavigationView.OnNavigationItemSelectedListener mNavigationListener;

  @Before public void setUp() {
    //noinspection unused
    new MockUp<View>() {
      @Mock View inflate(Context context, int resource, ViewGroup root) {
        return mEditText;
      }

      @Mock View findViewById(int id) {
        return mEditText;
      }
    };
    new MockUp<AppCompatActivity>() {
      @SuppressWarnings("unused") @Mock View findViewById(@IdRes int id) {
        View view = null;
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
        return true;
      }
    };

    uut = new BaseActivity() {
      @Override protected void searchByQuery(String query, CharSequence userInput) {
        mSearchingByQuery = true;
      }

      @Override public void searchByLocation(double lat, double lon) {
      }

      @Override public void loadOldData(List<Forecast> forecastList) {
        mRetrievingFromCache = true;
      }
    };
  }

  @Test public void shouldInitLocationManagerWhenCreatingTheActivity(
      @SuppressWarnings("UnusedParameters") @Mocked LocationManager mLocationManager, @Mocked View view) {

    whenCreatingTheActivity();

    thenLocationManagerShouldBeInstantiated(view);
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

  @Test
  public void shouldDelegateOnRequestPermissionsResult(@Mocked LocationManager mLocationManager) {
    givenActivityCreated();
    givenPermissionResultParameters();

    whenHandlingRequestPermissionsResult();

    thenShouldDelegateThePermissionsResult(mLocationManager);
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldSearchByQuery(@Mocked Toast toast, @Mocked Editable editable) {
    givenValidEditableInput(editable);
    givenHasInternet(true);

    whenSearchingByManualInput();

    thenShouldSearchByQuery(true);
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldInformNoInternetIfSearchByQueryAndLostConnectivity(@Mocked Toast toast) {
    givenValidEditableInput("");
    givenHasInternet(false);

    whenSearchingByManualInput();

    thenShouldSearchByQuery(false);
    thenShouldInformNoInternet();
  }

  @Test public void shouldNotPerformQueryAndInformUserForInvalidInput(@Mocked Toast toast,
      @Mocked Editable editable,
      @SuppressWarnings("UnusedParameters") @Mocked URLEncoder urlEncoder)
      throws UnsupportedEncodingException {
    givenInvalidEditableInput(editable);

    whenSearchingByManualInput();

    thenShouldShowTheToast(toast);
    thenShouldSearchByQuery(false);
  }

  @Test public void shouldCloseTheDrawerOnHomePressed(
      @SuppressWarnings("UnusedParameters") @Mocked Snackbar snackbar, @Mocked MenuItem item) {
    givenActivityCreated();

    whenPressingHome(item);

    thenShouldCloseDrawer();
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldDelegateActionWhenFabIsClicked(@Mocked AlertDialog alertDialog,
      @Mocked AlertDialog.Builder alertDialogBuilder) {
    givenFabClickListener();

    whenClickingTheFab();

    thenShouldCreateDialogToSearch();
  }

  @Test public void shouldCancelDialogOnCancel(@Mocked DialogInterface dialog,
      @SuppressWarnings("UnusedParameters") @Mocked AnalyticsEvent analyticsEvent) {
    givenCancelClickListener();

    whenClickingDialog(dialog);

    thenShouldCancel(dialog);
    thenShouldTrackEvent(MANUAL_SEARCH, "CANCEL");
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldSearchByManualInputOnDialogOk(@Mocked DialogInterface dialog, @Mocked Toast toast,
      @Mocked AnalyticsEvent analyticsEvent, @Mocked ConnectivityManager connectivityManager) {
    givenOkClickListener("someInput");
    givenHasInternet(true);

    whenClickingDialog(dialog);

    thenShouldTrackEvent(MANUAL_SEARCH, "someInput");
    thenShouldSearchByManualInput("someInput");
  }

  @Test public void shouldNavigateToSettingsWhenSelectingFromDrawer(@Mocked MenuItem item,
      @Mocked Intent intent) {
    givenNavigationListener();
    givenMenuItemId(item, R.id.drawer_settings);

    whenNavigationItemSelected(item);

    thenShouldNavigateToSettings(intent);
  }

  @Test
  public void shouldHandleHomePressedWhenSelectingFromDrawerNotKnownItem(@Mocked MenuItem item,
      @SuppressWarnings("UnusedParameters") @Mocked Snackbar snackbar) {
    givenActivityCreated();
    givenNavigationListener();
    givenMenuItemId(item, -1);

    whenNavigationItemSelected(item);

    thenShouldHandleHomePressed(item);
  }

  private void thenShouldTrackEvent(final String eventName, final String additionalDetails) {
    new Verifications() {{
      //noinspection WrongConstant
      new AnalyticsEvent(withEqual(eventName), withEqual(additionalDetails));
    }};
  }

  private void thenShouldHandleHomePressed(final MenuItem item) {
    new Verifications() {{
      uut.homePressed(item);
    }};
  }

  private void whenNavigationItemSelected(MenuItem item) {
    mNavigationListener.onNavigationItemSelected(item);
  }

  private void givenNavigationListener() {
    mNavigationListener = uut.getNavigationListener();
  }

  private void thenShouldSearchByManualInput(final CharSequence editable) {
    new Verifications() {{
      uut.searchByManualInput(withAny(editable));
    }};
  }

  private void givenOkClickListener(CharSequence editable) {
    mDialogClickListener = uut.getOkClickListener(editable);
  }

  private void thenShouldCancel(final DialogInterface dialog) {
    new Verifications() {{
      dialog.cancel();
    }};
  }

  private void whenClickingDialog(DialogInterface dialog) {
    mDialogClickListener.onClick(dialog, 1);
  }

  private void givenCancelClickListener() {
    mDialogClickListener = uut.getCancelListener();
  }

  private void thenShouldCreateDialogToSearch() {
    new Verifications() {{
      new AlertDialog.Builder(withInstanceLike(uut));
    }};
  }

  private void whenClickingTheFab() {
    mFabClickListener.onClick(null);
  }

  private void givenFabClickListener() {
    mFabClickListener = uut.getFabClickListener();
  }

  private void thenShouldCloseDrawer() {
    new Verifications() {{
      mDrawerLayout.closeDrawers();
    }};
  }

  private void whenPressingHome(MenuItem item) {
    uut.homePressed(item);
  }

  private void givenInvalidEditableInput(final Editable editable)
      throws UnsupportedEncodingException {
    mUserInput = editable;
    new Expectations() {{
      URLEncoder.encode(editable.toString(), "UTF-8");
      result = new UnsupportedEncodingException("");
    }};
  }

  private void thenShouldInformNoInternet() {
    new Verifications() {{
      uut.informNoInternet();
    }};
  }

  private void thenShouldSearchByQuery(boolean expected) {
    assertEquals(expected, mSearchingByQuery);
  }

  private void whenSearchingByManualInput() {
    uut.searchByManualInput(mUserInput);
  }

  private void givenHasInternet(final boolean hasInternet) {
    //noinspection unused
    new MockUp<WeatherUtils>() {
      @Mock
      boolean hasInternetConnection(Context context) {
        return hasInternet;
      }
    };
  }

  private void givenValidEditableInput(CharSequence editable) {
    mUserInput = editable;
  }

  private void thenShouldDelegateThePermissionsResult(final LocationManager locationManager) {
    new Verifications() {{
      locationManager.onRequestPermissionsResult(mRequestCode, mPermissions, mGrantResults);
    }};
  }

  private void whenHandlingRequestPermissionsResult() {
    uut.onRequestPermissionsResult(mRequestCode, mPermissions, mGrantResults);
  }

  private void givenPermissionResultParameters() {
    this.mRequestCode = 1;
    this.mPermissions = new String[] { "" };
    this.mGrantResults = new int[] { 1 };
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

  private void thenLocationManagerShouldBeInstantiated(final View view) {
    new Verifications() {{
      new LocationManager(uut, withAny(view));
    }};
  }

  private void whenCreatingTheActivity() {
    uut.onCreate(null);
  }
}
