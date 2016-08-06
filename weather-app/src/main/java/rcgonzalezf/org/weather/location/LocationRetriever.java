package rcgonzalezf.org.weather.location;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.common.BaseActivity;

import static rcgonzalezf.org.weather.utils.ForecastUtils.hasInternetConnection;

public class LocationRetriever
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = LocationRetriever.class.getSimpleName();

  private GoogleApiClient mGoogleApiClient;
  protected Location mLastLocation;
  private WeakReference<BaseActivity> mBaseActivity;
  private WeakReference<LocationRetrieverListener> mLocationRetrieverListener;

  public LocationRetriever(BaseActivity baseActivity,
      LocationRetrieverListener locationRetrieverListener) {
    this.mBaseActivity = new WeakReference<>(baseActivity);
    this.mLocationRetrieverListener = new WeakReference<>(locationRetrieverListener);

    buildGoogleApiClient();
  }

  @Override public void onConnected(Bundle bundle) {
    mLocationRetrieverListener.get().checkForPermissions();
  }

  @Override public void onConnectionSuspended(int i) {
    Log.d(TAG, "Google Location onConnectionSuspended " + i);
  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.d(TAG, "Google Location onConnectionFailed " + connectionResult.getErrorMessage());
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(mBaseActivity.get()).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
  }

  // We are handling the potential missing permission
  @SuppressWarnings("MissingPermission") void tryToUseLastKnownLocation() {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (!hasInternetConnection(mBaseActivity.get())) {
      mBaseActivity.get().informNoInternet();
    } else if (mLastLocation != null) {

      double lat = mLastLocation.getLatitude();
      double lon = mLastLocation.getLongitude();

      mLocationRetrieverListener.get().onLocationFound(lat, lon);
    } else {
      mLocationRetrieverListener.get().onEmptyLocation();
    }
  }

  public void connect() {
    mGoogleApiClient.connect();
  }

  public void disconnect() {
    mGoogleApiClient.disconnect();
  }

  public void onLocationPermissionsGranted() {
    tryToUseLastKnownLocation();
  }
}
