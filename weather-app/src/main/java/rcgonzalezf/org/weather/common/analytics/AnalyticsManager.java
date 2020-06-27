package rcgonzalezf.org.weather.common.analytics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import java.util.HashSet;
import java.util.Set;
import rcgonzalezf.org.weather.BuildConfig;
import rcgonzalezf.org.weather.utils.WeatherUtils;

public class AnalyticsManager {

  static final String MOBILE = "Mobile";
  static final String UNKNOWN = "Unknown";
  static final String NONE = "None";
  static final String WIFI = "Wifi";

  public static final String ANDROID_VERSION = "ANDROID_VERSION";
  public static final String APP_VERSION = "APP_VERSION";
  public static final String NETWORK = "NETWORK";
  public static final String MULTIPANE = "MULTIPANE";

  @VisibleForTesting
  static String sAndroidVersion = Build.VERSION.RELEASE;
  @VisibleForTesting static String sAppVersion = BuildConfig.VERSION_NAME;
  private Context mContext;
  private Set<AnalyticsObserver> mAnalyticsObservers = new HashSet<>();
  private AnalyticsBaseData mAnalyticsBaseData;
  private String mScreenName;

  public AnalyticsManager(Context context) {
    mContext = context;
    mAnalyticsBaseData = new AnalyticsBaseData();
    mAnalyticsBaseData.data().put(ANDROID_VERSION, sAndroidVersion);
    mAnalyticsBaseData.data().put(APP_VERSION, sAppVersion);
    mAnalyticsBaseData.data().put(MULTIPANE, Boolean.toString(isMultipane()));
  }

  public void addObserver(AnalyticsObserver analyticsObserver) {
    mAnalyticsObservers.add(analyticsObserver);
  }

  private boolean isMultipane() {
    return WeatherUtils.isXLargeTablet(mContext);
  }

  void notifyOnScreenLoad(String screenName) {
    mScreenName = screenName;
    addBaseData();

    for (AnalyticsObserver analyticsObserver : mAnalyticsObservers) {
      analyticsObserver.onScreen(screenName, mAnalyticsBaseData);
    }
  }

  @NonNull
  @NetworkType private String getNetworkType() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
    String networkType;

    if (activeNetwork != null) {
      if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
        networkType = WIFI;
      } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
        networkType = MOBILE;
      } else {
        networkType = UNKNOWN;
      }
    } else {
      networkType = NONE;
    }
    return networkType;
  }

  void removeObserver(AnalyticsObserver analyticsObserver) {
    mAnalyticsObservers.remove(analyticsObserver);
  }

  void notifyOnAction(AnalyticsEvent analyticsEvent) {
    addBaseData();
    for (AnalyticsObserver analyticsObserver : mAnalyticsObservers) {
      analyticsObserver.onAction(analyticsEvent, mScreenName, mAnalyticsBaseData);
    }
  }

  private void addBaseData() {
    mAnalyticsBaseData.data().put(NETWORK, getNetworkType());
  }
}
