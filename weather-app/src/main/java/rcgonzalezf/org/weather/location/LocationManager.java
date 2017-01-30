package rcgonzalezf.org.weather.location;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.view.View;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.common.PermissionChecker;
import rcgonzalezf.org.weather.common.PermissionResultListener;

public class LocationManager implements LocationRetrieverListener {

  @VisibleForTesting static int CURRENT_SDK_INT = Build.VERSION.SDK_INT;
  private final WeakReference<BaseActivity> baseActivityWeakReference;
  private final WeakReference<View> mContentWeakReference;
  private final LocationRetriever mLocationRetriever;
  @VisibleForTesting PermissionChecker mPermissionChecker;

  public LocationManager(@NonNull BaseActivity baseActivity, View content) {
    this.baseActivityWeakReference = new WeakReference<>(baseActivity);
    this.mContentWeakReference = new WeakReference<>(content);
    this.mLocationRetriever = new LocationRetriever(baseActivity, this);
  }

  @Override public void checkForPermissions() {
    BaseActivity baseActivity = baseActivityWeakReference.get();
    View content = mContentWeakReference.get();
    checkForPermissions(baseActivity, content);
  }

  @Override public void onEmptyLocation() {
    BaseActivity baseActivity = baseActivityWeakReference.get();
    View content = mContentWeakReference.get();
    onEmptyLocation(baseActivity, content);
  }

  @Override public void onLocationFound(double lat, double lon) {
    BaseActivity baseActivity = baseActivityWeakReference.get();
    onLocationFound(baseActivity, lat, lon);
  }

  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (mPermissionChecker != null && CURRENT_SDK_INT >= Build.VERSION_CODES.M) {
      //noinspection NewApi
      mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  public void connect() {
    mLocationRetriever.connect();
  }

  public void disconnect() {
    mLocationRetriever.disconnect();
  }

  @VisibleForTesting @NonNull PermissionResultListener getPermissionResultListener() {
    return new PermissionResultListener() {

      @Override public void onSuccess() {
        mLocationRetriever.onLocationPermissionsGranted();
      }

      @Override public void onFailure() {
        mLocationRetriever.onLocationPermissionFailure();
      }
    };
  }

  @VisibleForTesting void checkForPermissions(BaseActivity baseActivity, View content) {
    if (baseActivity != null && content != null) {
      mPermissionChecker =
          new PermissionChecker(Manifest.permission.ACCESS_FINE_LOCATION, baseActivity,
              PermissionChecker.LOCATION, content, R.string.permissions_location_granted,
              R.string.permissions_location_not_granted, R.string.permissions_location_rationale);

      if (mPermissionChecker.hasPermission()) {
        mLocationRetriever.onLocationPermissionsGranted();
      } else {
        mPermissionChecker.requestPermission(getPermissionResultListener());
      }
    }
  }

  @VisibleForTesting void onEmptyLocation(final BaseActivity baseActivity, View content) {
    if (baseActivity != null && content != null) {
      Snackbar.make(content, baseActivity.getString(R.string.location_off_msg),
          Snackbar.LENGTH_SHORT).show();
    }
  }

  @VisibleForTesting void onLocationFound(BaseActivity baseActivity, double lat, double lon) {
    if (baseActivity != null) {
      baseActivity.searchByLocation(lat, lon);
    }
  }
}
