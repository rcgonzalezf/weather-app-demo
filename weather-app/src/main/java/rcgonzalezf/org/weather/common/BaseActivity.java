package rcgonzalezf.org.weather.common;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.Utils;
import rcgonzalezf.org.weather.WeatherActivity;

public abstract class BaseActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    ApiCallback {

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
    final ImageView avatar = (ImageView) findViewById(R.id.avatar);
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
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if(!Utils.hasInternetConnection(this)) {
      Toast.makeText(this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
    }
    if (mLastLocation != null) {
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

  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {

  }


  private void initToolbar() {
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void setupDrawerLayout() {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
    view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.drawer_settings) {
          Intent intent = new Intent(BaseActivity.this, WeatherActivity.class);
          startActivity(intent);
        } else {
          Snackbar.make(mContent, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
          menuItem.setChecked(true);
          mDrawerLayout.closeDrawers();
        }
        return true;
      }
    });
  }
}
