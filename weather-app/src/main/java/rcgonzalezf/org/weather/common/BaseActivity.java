package rcgonzalezf.org.weather.common;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.location.LocationRetriever;
import rcgonzalezf.org.weather.location.LocationRetrieverListener;
import rcgonzalezf.org.weather.models.Forecast;

import static rcgonzalezf.org.weather.SettingsActivity.USER_NAME_TO_DISPLAY;
import static rcgonzalezf.org.weather.utils.WeatherUtils.hasInternetConnection;

public abstract class BaseActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, OnOfflineLoader {

  protected static final String OFFLINE_FILE = "OFFLINE_WEATHER";
  public static final String FORECASTS = "FORECASTS";
  private static final String TAG = BaseActivity.class.getSimpleName();

  private DrawerLayout mDrawerLayout;
  private View mContent;
  private LocationRetriever mLocationRetriever;
  private PermissionChecker mPermissionChecker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.weather);
    mLocationRetriever = new LocationRetriever(this, new LocationListener());

    initToolbar();
    setupDrawerLayout();
    setupFabButton();

    mContent = findViewById(R.id.content);
  }

  @Override protected void onStart() {
    super.onStart();
    mLocationRetriever.connect();
  }

  @Override protected void onStop() {
    mLocationRetriever.disconnect();
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
    if (textView != null) {
      textView.setText(
          prefs.getString(USER_NAME_TO_DISPLAY, getString(R.string.pref_default_display_name)));
    }
  }

  private void setupFabButton() {
    findViewById(R.id.main_fab).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        performFabAction();
      }
    });
  }

  protected void performFabAction() {
    View promptsView = View.inflate(this, R.layout.dialog_city_query, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    final EditText userInput = (EditText) promptsView.findViewById(R.id.city_input_edit_text);

    alertDialogBuilder.setView(promptsView);
    alertDialogBuilder.setCancelable(false)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {

            String query;
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

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (mPermissionChecker != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      //noinspection NewApi
      mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  public class LocationListener implements LocationRetrieverListener {

    @Override public void checkForPermissions() {

      mPermissionChecker =
          new PermissionChecker(Manifest.permission.ACCESS_FINE_LOCATION, BaseActivity.this,
              PermissionChecker.LOCATION, mContent, R.string.permissions_location_granted,
              R.string.permissions_location_not_granted, R.string.permissions_location_rationale);

      if (mPermissionChecker.hasPermission()) {
        mLocationRetriever.onLocationPermissionsGranted();
      } else {
        mPermissionChecker.requestPermission(new PermissionResultListener() {

          @Override public void onSuccess() {
            mLocationRetriever.onLocationPermissionsGranted();
          }

          @Override public void onFailure() {
          }
        });
      }
    }

    @Override public void onEmptyLocation() {
      Snackbar.make(mContent, getString(R.string.location_off_msg), Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onLocationFound(double lat, double lon) {
     searchByLocation(lat, lon);
    }
  }

  protected abstract void searchByQuery(String query, EditText userInput);
  protected abstract void searchByLocation(double lat, double lon);

}
