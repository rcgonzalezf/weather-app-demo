package rcgonzalezf.org.weather.common;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;

public abstract class BaseActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    ApiCallback {

  private GoogleApiClient mGoogleApiClient;
  protected Location mLastLocation;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    buildGoogleApiClient();
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

  @Override public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (mLastLocation != null) {
      mLastLocation.getLatitude();
      mLastLocation.getLongitude();

      WeatherRepository<OpenWeatherApiRequestParameters> weatherRepository =
          ServiceConfig.getInstance().getWeatherRepository();

      weatherRepository.findWeather(new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
          .withLatLon(mLastLocation.getLatitude(), mLastLocation.getLongitude()).build(), this);

    }
  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {

  }
}
