package rcgonzalezf.org.weather.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.models.Forecast;

import static rcgonzalezf.org.weather.SettingsActivity.USER_NAME_TO_DISPLAY;
import static rcgonzalezf.org.weather.utils.ForecastUtils.hasInternetConnection;

public abstract class BaseActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    ApiCallback, OnOfflineLoader {

  protected static final String OFFLINE_FILE = "OFFLINE_WEATHER";
  public static final String FORECASTS = "FORECASTS";
  private static final String TAG = BaseActivity.class.getSimpleName();

  protected Location mLastLocation;
  private GoogleApiClient mGoogleApiClient;
  private DrawerLayout mDrawerLayout;
  private View mContent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.weather);
    buildGoogleApiClient();

    initToolbar();
    setupDrawerLayout();
    setupFabButton();

    mContent = findViewById(R.id.content);
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
  }

  @Override protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override protected void onStop() {
    mGoogleApiClient.disconnect();
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

  private void navigateToSettings() {
    Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
    startActivity(intent);
  }

  @Override public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (!hasInternetConnection(this)) {
      informNoInternet();
    } else if (mLastLocation != null) {
      WeatherRepository<OpenWeatherApiRequestParameters> weatherRepository =
          ServiceConfig.getInstance().getWeatherRepository();

      weatherRepository.findWeather(
          new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withLatLon(
              mLastLocation.getLatitude(), mLastLocation.getLongitude()).build(), this);
    } else {
      Snackbar.make(mContent, getString(R.string.location_off_msg), Snackbar.LENGTH_SHORT).show();
    }
  }

  @Override public void onConnectionSuspended(int i) {
    Log.d(TAG, "Google Location onConnectionSuspended " + i);
  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.d(TAG, "Google Location onConnectionFailed " + connectionResult.getErrorMessage());
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
    view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.drawer_settings) {
          navigateToSettings();
        } else {
          Snackbar.make(mContent, menuItem.getTitle() + " pressed", Snackbar.LENGTH_SHORT).show();
          menuItem.setChecked(true);
          mDrawerLayout.closeDrawers();
        }
        return true;
      }
    });

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    TextView textView = (TextView) findViewById(R.id.user_display_name);
    textView.setText(
        prefs.getString(USER_NAME_TO_DISPLAY, getString(R.string.pref_default_display_name)));
  }

  private void setupFabButton() {
    findViewById(R.id.main_fab).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        performFabAction(v);
      }
    });
  }

  protected void performFabAction(View view) {
    LayoutInflater li = LayoutInflater.from(this);
    View promptsView = li.inflate(R.layout.dialog_city_query, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    alertDialogBuilder.setView(promptsView);

    final EditText userInput = (EditText) promptsView.findViewById(R.id.city_input_edit_text);

    alertDialogBuilder.setCancelable(false)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {

            String query = null;
            try {
              query = URLEncoder.encode(userInput.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
              Log.e(TAG, "Can't encode URL", e);
              Toast.makeText(BaseActivity.this,
                  getString(R.string.invalid_input) + ": " + userInput.getText() + "...",
                  Toast.LENGTH_SHORT).show();
              return;
            }

            if (!hasInternetConnection(BaseActivity.this)) {
              informNoInternet();
            } else {
              searchByQuery(query, userInput);
            }
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  protected void searchByQuery(String query, EditText userInput) {
    WeatherRepository<OpenWeatherApiRequestParameters> weatherRepository =
        ServiceConfig.getInstance().getWeatherRepository();

    weatherRepository.findWeather(
        new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityName(
            query).build(), BaseActivity.this);

    Toast.makeText(BaseActivity.this,
        getString(R.string.searching) + " " + userInput.getText() + "...", Toast.LENGTH_SHORT).show();
  }

  protected void informNoInternet() {
    Toast.makeText(this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
    final List<Forecast> forecastList = getPreviousForecastList();
    loadOldData(forecastList);
  }

  public List<Forecast> getPreviousForecastList() {
    SharedPreferences sharedPreferences = getSharedPreferences(OFFLINE_FILE, 0);
    String serializedData = sharedPreferences.getString(FORECASTS, null);
    List<Forecast> storedData = null;
    if (serializedData != null) {
      try {
        ByteArrayInputStream input =
            new ByteArrayInputStream(Base64.decode(serializedData, Base64.DEFAULT));
        ObjectInputStream inputStream = new ObjectInputStream(input);
        storedData = (ArrayList<Forecast>) inputStream.readObject();
      } catch (IOException | ClassNotFoundException | java.lang.IllegalArgumentException e) {
        Log.e(TAG, "Can't retrive previous offline data", e);
      }
    }

    return storedData;
  }
}
