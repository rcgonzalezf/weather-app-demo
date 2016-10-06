package rcgonzalezf.org.weather.common.analytics;

import android.content.Context;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import rcgonzalezf.org.weather.BuildConfig;
import rcgonzalezf.org.weather.utils.WeatherUtils;

public class AnalyticsManager {
  public static final String ANDROID_VERSION = "ANDROID_VERSION";
  public static final String APP_VERSION = "APP_VERSION";
  public static final String NETWORK = "NETWORK";
  public static final String MULTIPANE = "MULTIPANE";

  @VisibleForTesting
  static String sAndroidVersion = Build.VERSION.RELEASE;
  @VisibleForTesting
  static String sAppVersion = BuildConfig.VERSION_NAME;
  private Context mContext;

  public AnalyticsManager(Context context) {
    mContext = context;
  }

  public void addObserver(AnalyticsObserver analyticsObserver) {
    
  }

  public boolean isMultipane() {
    return WeatherUtils.isXLargeTablet(mContext);
  }

  public void notifyOnScreenLoad(String screenName) {

  }

  public String getNetworkType() {
    return null;
  }

  public void removeObserver(AnalyticsObserver analyticsObserver) {

  }

  public void notifyOnAction(String action) {

  }
}
