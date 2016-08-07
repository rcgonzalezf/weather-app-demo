package rcgonzalezf.org.weather.location;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.common.BaseActivity;

import static rcgonzalezf.org.weather.utils.WeatherUtils.hasInternetConnection;

public class LocationRetriever
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = LocationRetriever.class.getSimpleName();

  private GoogleApiClient mGoogleApiClient;
  private WeakReference<BaseActivity> mWeakBaseActivity;
  private WeakReference<LocationRetrieverListener> mWeakLocationRetrieverListener;

  public LocationRetriever(BaseActivity baseActivity,
      LocationRetrieverListener locationRetrieverListener) {
    this.mWeakBaseActivity = new WeakReference<>(baseActivity);
    this.mWeakLocationRetrieverListener = new WeakReference<>(locationRetrieverListener);

    buildGoogleApiClient();
  }

  @Override public void onConnected(Bundle bundle) {
    final LocationRetrieverListener locationRetrieverListener =
        mWeakLocationRetrieverListener.get();
    if (locationRetrieverListener != null) {
      locationRetrieverListener.checkForPermissions();
    }
  }

  @Override public void onConnectionSuspended(int i) {
    Log.d(TAG, "Google Location onConnectionSuspended " + i);
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "Google Location onConnectionFailed " + connectionResult.getErrorMessage());
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

  private synchronized void buildGoogleApiClient() {
    final BaseActivity baseActivity = mWeakBaseActivity.get();
    if (baseActivity != null) {
      mGoogleApiClient = new GoogleApiClient.Builder(baseActivity).addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API)
          .build();
    }
  }

  private void tryToUseLastKnownLocation() {
    final BaseActivity baseActivity = mWeakBaseActivity.get();
    final LocationRetrieverListener locationRetrieverListener =
        mWeakLocationRetrieverListener.get();

    if (baseActivity != null && !hasInternetConnection(baseActivity)) {
      baseActivity.informNoInternet();
    } else if (locationRetrieverListener != null) {
      useLastLocation(locationRetrieverListener);
    }
  }

  // We are handling the potential missing permission
  @SuppressWarnings("MissingPermission") private void useLastLocation(
      LocationRetrieverListener locationRetrieverListener) {
    final Location mLastLocation =
        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (mLastLocation != null) {
      double lat = mLastLocation.getLatitude();
      double lon = mLastLocation.getLongitude();

      locationRetrieverListener.onLocationFound(lat, lon);
    } else {
      locationRetrieverListener.onEmptyLocation();
    }
  }
}
