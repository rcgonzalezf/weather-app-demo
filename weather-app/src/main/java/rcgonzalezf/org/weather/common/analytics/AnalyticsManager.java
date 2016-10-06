package rcgonzalezf.org.weather.common.analytics;

import android.os.Build;
import android.support.annotation.VisibleForTesting;
import rcgonzalezf.org.weather.BuildConfig;

public class AnalyticsManager {
  @VisibleForTesting
  static String sAndroidVersion = Build.VERSION.RELEASE;
  @VisibleForTesting
  static String sAppVersion = BuildConfig.VERSION_NAME;

  public void addObserver(AnalyticsObserver mAnalyticsTestObserver) {
    
  }
}
