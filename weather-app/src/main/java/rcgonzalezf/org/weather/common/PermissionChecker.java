package rcgonzalezf.org.weather.common;

import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import com.google.android.material.snackbar.Snackbar;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.R;

public class PermissionChecker implements ActivityCompat.OnRequestPermissionsResultCallback {

  public static final int LOCATION = 10;

  private final WeakReference<BaseActivity> mWeakContext;
  private String mPermission;
  private int mRequestCode;
  private WeakReference<View> mContainer;
  private int mPermissionGrantedMessageId;
  private int mPermissionsNotGrantedMessageId;
  private int mPermissionRationaleMessageId;
  private PermissionResultListener mPermissionResultListener;

  public PermissionChecker(@NonNull String permission, @NonNull BaseActivity activity,
                           int requestCode, @NonNull View container, @StringRes int permissionGrantedMessageId,
                           @StringRes int permissionsNotGrantedMessageId, @StringRes int permissionRationaleMessageId) {
    this.mWeakContext = new WeakReference<>(activity);
    this.mPermission = permission;
    this.mRequestCode = requestCode;
    this.mContainer = new WeakReference<>(container);
    this.mPermissionGrantedMessageId = permissionGrantedMessageId;
    this.mPermissionsNotGrantedMessageId = permissionsNotGrantedMessageId;
    this.mPermissionRationaleMessageId = permissionRationaleMessageId;
  }

  public boolean hasPermission() {
    return ActivityCompat.checkSelfPermission(mWeakContext.get(), mPermission)
        == PackageManager.PERMISSION_GRANTED;
  }

  public void requestPermission(PermissionResultListener permissionResultListener) {
    this.mPermissionResultListener = permissionResultListener;

    if (ActivityCompat.shouldShowRequestPermissionRationale(mWeakContext.get(), mPermission)) {

      Snackbar.make(mContainer.get(), mPermissionRationaleMessageId, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.ok, getSnackBarClickListener())
          .show();
    } else {
      requestPermissions();
    }
  }

  @VisibleForTesting
  @NonNull View.OnClickListener getSnackBarClickListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View view) {
        requestPermissions();
      }
    };
  }

  void requestPermissions() {
    ActivityCompat.requestPermissions(mWeakContext.get(), new String[] { mPermission },
        mRequestCode);
  }

  @RequiresApi(api = Build.VERSION_CODES.M) @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    if (this.mRequestCode == requestCode) {
      int messageResId;
      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        messageResId = mPermissionGrantedMessageId;
        handleGranted();
      } else {
        messageResId = mPermissionsNotGrantedMessageId;
        handleRejected();
      }
      Snackbar.make(mContainer.get(), messageResId, Snackbar.LENGTH_SHORT)
          .show();
    } else {
      mWeakContext.get().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @VisibleForTesting
  void handleRejected() {
    if (mPermissionResultListener != null) {
      mPermissionResultListener.onFailure();
    }
  }

  @VisibleForTesting
  void handleGranted() {
    if (mPermissionResultListener != null) {
      mPermissionResultListener.onSuccess();
    }
  }
}
