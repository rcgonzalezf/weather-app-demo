package rcgonzalezf.org.weather.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import java.lang.ref.WeakReference;

public class PermissionChecker {

  private final WeakReference<Context> weakContext;
  private String permission;

  public PermissionChecker(String permission, Context context) {
    this.weakContext = new WeakReference<>(context);
    this.permission = permission;
  }

  public boolean hasPermission() {
    return ActivityCompat.checkSelfPermission(weakContext.get(),
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }
}
