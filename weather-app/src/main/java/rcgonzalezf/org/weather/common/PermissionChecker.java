package rcgonzalezf.org.weather.common;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.R;

public class PermissionChecker implements ActivityCompat.OnRequestPermissionsResultCallback {

  public static final int LOCATION = 10;

  private final WeakReference<Activity> weakContext;
  private String permission;
  private int requestCode;
  private WeakReference<View> container;
  private int permissionGrantedMessageId;
  private int permissionsNotGrantedMessageId;
  private int permissionRationaleMessageId;
  private PermissionResultListener permissionResultListener;

  public PermissionChecker(@NonNull String permission,
      @NonNull Activity activity,
      int requestCode,
      @NonNull View container,
      @StringRes int permissionGrantedMessageId,
      @StringRes int permissionsNotGrantedMessageId,
      @StringRes int permissionRationaleMessageId) {
    this.weakContext = new WeakReference<>(activity);
    this.permission = permission;
    this.requestCode = requestCode;
    this.container = new WeakReference<>(container);
    this.permissionGrantedMessageId = permissionGrantedMessageId;
    this.permissionsNotGrantedMessageId = permissionsNotGrantedMessageId;
    this.permissionRationaleMessageId = permissionRationaleMessageId;
  }

  public boolean hasPermission() {
    return ActivityCompat.checkSelfPermission(weakContext.get(), permission)
        == PackageManager.PERMISSION_GRANTED;
  }

  public void requestLocationPermission(PermissionResultListener permissionResultListener) {
    this.permissionResultListener = permissionResultListener;

    if (ActivityCompat.shouldShowRequestPermissionRationale(weakContext.get(), permission)) {

      Snackbar.make(container.get(), permissionRationaleMessageId, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.ok, new View.OnClickListener() {
            @Override public void onClick(View view) {
              ActivityCompat.requestPermissions(weakContext.get(), new String[] { permission },
                  requestCode);
            }
          })
          .show();
    } else {
      ActivityCompat.requestPermissions(weakContext.get(), new String[] { permission },
          requestCode);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    if (this.requestCode == requestCode) {

      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Snackbar.make(container.get(), permissionGrantedMessageId, Snackbar.LENGTH_SHORT).show();
        if (permissionResultListener != null) permissionResultListener.onSuccess();
      } else {
        Snackbar.make(container.get(), permissionsNotGrantedMessageId, Snackbar.LENGTH_SHORT)
            .show();
        if (permissionResultListener != null) permissionResultListener.onFailure();
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      weakContext.get().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
}
