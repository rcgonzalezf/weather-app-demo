package rcgonzalezf.org.weather.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import java.lang.ref.WeakReference;
import rcgonzalezf.org.weather.R;

public class PermissionChecker {

  private final WeakReference<Context> weakContext;
  private String permission;

  public PermissionChecker(String permission, Context context) {
    this.weakContext = new WeakReference<>(context);
    this.permission = permission;
  }

  public boolean hasPermission() {
    return ActivityCompat.checkSelfPermission(weakContext.get(), permission)
        == PackageManager.PERMISSION_GRANTED;
  }

  public void requestLocationPermission(View container) {
    Snackbar.make(container, weakContext.get().getString(R.string.permissions_location_off),
        Snackbar.LENGTH_SHORT).show();
  }
}
