package rcgonzalezf.org.weather.location;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.common.BaseActivity;

import static rcgonzalezf.org.weather.utils.WeatherUtils.hasInternetConnection;

class LocationRetriever
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = LocationRetriever.class.getSimpleName();

  private GoogleApiClient mGoogleApiClient;
  private WeakReference<BaseActivity> mWeakBaseActivity;
  private WeakReference<LocationRetrieverListener> mWeakLocationRetrieverListener;

  LocationRetriever(@NonNull BaseActivity baseActivity,
      LocationRetrieverListener locationRetrieverListener) {
    mWeakBaseActivity = new WeakReference<>(baseActivity);
    mWeakLocationRetrieverListener = new WeakReference<>(locationRetrieverListener);
    mGoogleApiClient = new GoogleApiClient.Builder(baseActivity).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
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

  void connect() {
    mGoogleApiClient.connect();
  }

  void disconnect() {
    mGoogleApiClient.disconnect();
  }

  void onLocationPermissionsGranted() {
    final BaseActivity baseActivity = mWeakBaseActivity.get();
    tryToUseLastKnownLocation(baseActivity);
  }

  @VisibleForTesting
  void tryToUseLastKnownLocation(BaseActivity baseActivity) {
    final LocationRetrieverListener locationRetrieverListener =
        mWeakLocationRetrieverListener.get();

    if (baseActivity != null) {
      if (!hasInternetConnection(baseActivity)) {
        baseActivity.informNoInternet();
      } else if (locationRetrieverListener != null) {
        useLastLocation(locationRetrieverListener);
      }
    }
  }

  private void useLastLocation(LocationRetrieverListener locationRetrieverListener) {
    final Location mLastLocation = getLastLocation();
    if (mLastLocation != null) {
      double lat = mLastLocation.getLatitude();
      double lon = mLastLocation.getLongitude();

      locationRetrieverListener.onLocationFound(lat, lon);
    } else {
      locationRetrieverListener.onEmptyLocation();
    }
  }

  // We are handling the potential missing permission
  @VisibleForTesting @SuppressWarnings("MissingPermission") Location getLastLocation() {
    return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
  }
}
