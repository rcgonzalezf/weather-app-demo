package rcgonzalezf.org.weather.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newrelic.agent.android.NewRelic;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import org.rcgonzalezf.weather.common.models.Forecast;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.location.LocationManager;

import static rcgonzalezf.org.weather.SettingsActivity.USER_NAME_TO_DISPLAY;
import static rcgonzalezf.org.weather.utils.WeatherUtils.hasInternetConnection;

public abstract class BaseActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, OnOfflineLoader {

  protected static final String OFFLINE_FILE = "OFFLINE_WEATHER";
  public static final String FORECASTS = "FORECASTS";
  private static final String TAG = BaseActivity.class.getSimpleName();

  private DrawerLayout mDrawerLayout;
  private View mContent;
  private LocationManager mLocationManager;

  protected abstract void searchByQuery(String query, Editable userInput);

  public abstract void searchByLocation(double lat, double lon);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.weather);
    initToolbar();
    setupDrawerLayout();
    findViewById(R.id.main_fab).setOnClickListener(getFabClickListener());

    mContent = findViewById(R.id.content);
    mLocationManager = new LocationManager(this, mContent);

    NewRelic.withApplicationToken(getString(R.string.newrelic_api_key))
        .start(getApplicationContext());
  }

  @Override protected void onStart() {
    super.onStart();
    mLocationManager.connect();
  }

  @Override protected void onStop() {
    mLocationManager.disconnect();
    super.onStop();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
      case R.id.action_settings:
        navigateToSettings();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  protected void initToolbar() {
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  protected void setupDrawerLayout() {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
    view.setNavigationItemSelectedListener(getNavigationListener());

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    TextView textView = (TextView) findViewById(R.id.user_display_name);
    if (textView != null) {
      textView.setText(
          prefs.getString(USER_NAME_TO_DISPLAY, getString(R.string.pref_default_display_name)));
    }
  }

  protected void performFabAction() {
    View promptsView = View.inflate(this, R.layout.dialog_city_query, null);
    final EditText userInput = (EditText) promptsView.findViewById(R.id.city_input_edit_text);

    new AlertDialog.Builder(this).setView(promptsView)
        .setCancelable(false)
        .setPositiveButton("OK", getOkClickListener(userInput.getText()))
        .setNegativeButton("Cancel", getCancelListener())
        .create()
        .show();
  }

  public void informNoInternet() {
    Toast.makeText(this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
    final List<Forecast> forecastList = getPreviousForecastList();
    loadOldData(forecastList);
  }

  public List<Forecast> getPreviousForecastList() {
    SharedPreferences sharedPreferences = getSharedPreferences(OFFLINE_FILE, 0);
    String serializedData = sharedPreferences.getString(FORECASTS, null);
    List<Forecast> storedData = null;
    if (serializedData != null) {
      storedData = new Gson().fromJson(serializedData, new TypeToken<List<Forecast>>() {
      }.getType());
    }
    return storedData;
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    mLocationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @VisibleForTesting void searchByManualInput(Editable userInput) {
    String query;
    try {
      query = URLEncoder.encode(userInput.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      Log.e(TAG, "Can't encode URL", e);
      Toast.makeText(BaseActivity.this,
          getString(R.string.invalid_input) + ": " + userInput + "...", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!hasInternetConnection(BaseActivity.this)) {
      informNoInternet();
    } else {
      searchByQuery(query, userInput);
    }
  }

  @VisibleForTesting void homePressed(MenuItem menuItem) {
    Snackbar.make(mContent, menuItem.getTitle() + " pressed", Snackbar.LENGTH_SHORT).show();
    menuItem.setChecked(true);
    mDrawerLayout.closeDrawers();
  }

  @VisibleForTesting @NonNull View.OnClickListener getFabClickListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        performFabAction();
      }
    };
  }

  @VisibleForTesting @NonNull DialogInterface.OnClickListener getCancelListener() {
    return new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
      }
    };
  }

  @VisibleForTesting @NonNull DialogInterface.OnClickListener getOkClickListener(
      final Editable userInput) {
    return new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        searchByManualInput(userInput);
      }
    };
  }

  @VisibleForTesting @NonNull
  NavigationView.OnNavigationItemSelectedListener getNavigationListener() {
    return new NavigationView.OnNavigationItemSelectedListener() {
      @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.drawer_settings) {
          navigateToSettings();
        } else {
          homePressed(menuItem);
        }
        return true;
      }
    };
  }

  private void navigateToSettings() {
    Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
    startActivity(intent);
  }
}
