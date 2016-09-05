package rcgonzalezf.org.weather.common;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
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

  public PermissionChecker(@NonNull String permission, @NonNull BaseActivity activity, int requestCode,
      @NonNull View container, @StringRes int permissionGrantedMessageId,
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
          .setAction(R.string.ok, new View.OnClickListener() {
            @Override public void onClick(View view) {
              requestPermissions();
            }
          })
          .show();
    } else {
      requestPermissions();
    }
  }

  void requestPermissions() {
    ActivityCompat.requestPermissions(mWeakContext.get(), new String[] { mPermission },
        mRequestCode);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    if (this.mRequestCode == requestCode) {

      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Snackbar.make(mContainer.get(), mPermissionGrantedMessageId, Snackbar.LENGTH_SHORT).show();
        if (mPermissionResultListener != null) {
          mPermissionResultListener.onSuccess();
        }
      } else {
        Snackbar.make(mContainer.get(), mPermissionsNotGrantedMessageId, Snackbar.LENGTH_SHORT)
            .show();
        if (mPermissionResultListener != null) {
          mPermissionResultListener.onFailure();
        }
      }
    } else {
      mWeakContext.get().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
}
