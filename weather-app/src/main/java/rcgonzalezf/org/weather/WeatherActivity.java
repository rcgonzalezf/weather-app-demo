package rcgonzalezf.org.weather;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class WeatherActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private GoogleApiClient mGoogleApiClient;
  private Location mLastLocation;
  private TextView mLatitudeText;
  private TextView mLongitudeText;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.weather);
    mLatitudeText = (TextView) findViewById(R.id.latitude);
    mLongitudeText = (TextView) findViewById(R.id.longitude);
    buildGoogleApiClient();
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
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
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
        mGoogleApiClient);
    if (mLastLocation != null) {
      mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
      mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
    }
  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {

  }
}
