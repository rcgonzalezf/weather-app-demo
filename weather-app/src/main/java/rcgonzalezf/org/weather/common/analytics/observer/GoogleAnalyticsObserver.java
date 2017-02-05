package rcgonzalezf.org.weather.common.analytics.observer;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.WeatherApp;
import rcgonzalezf.org.weather.common.analytics.AnalyticsBaseData;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager;
import rcgonzalezf.org.weather.common.analytics.AnalyticsObserver;

public class GoogleAnalyticsObserver implements AnalyticsObserver {

  private Tracker mTracker;
  private static final int OS_VERSION = 1;
  private static final int APP_VERSION = 2;
  private static final int NETWORK = 3;
  private static final int MULTIPANE = 4;

  synchronized private Tracker getDefaultTracker() {
    if (mTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(WeatherApp.getInstance());
      // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
      mTracker = analytics.newTracker(R.xml.global_tracker);
      mTracker.setAnonymizeIp(true);
    }
    return mTracker;
  }

  @Override public void onScreen(String screenName, AnalyticsBaseData analyticsBaseData) {
    mTracker = getDefaultTracker();
    mTracker.setScreenName(screenName);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Override public void onAction(AnalyticsEvent analyticsEvent, String screenName,
      AnalyticsBaseData analyticsBaseData) {
    HitBuilders.EventBuilder builder =
        new HitBuilders.EventBuilder().setCategory(analyticsEvent.name);

    if (analyticsEvent.additionalValue != null) {
      builder.setAction(analyticsEvent.additionalValue);
    }

    builder.setCustomDimension(OS_VERSION,
        analyticsBaseData.data().get(AnalyticsManager.ANDROID_VERSION));
    builder.setCustomDimension(APP_VERSION,
        analyticsBaseData.data().get(AnalyticsManager.APP_VERSION));
    builder.setCustomDimension(NETWORK, analyticsBaseData.data().get(AnalyticsManager.NETWORK));
    builder.setCustomDimension(MULTIPANE, analyticsBaseData.data().get(AnalyticsManager.MULTIPANE));

    mTracker = getDefaultTracker();
    mTracker.setScreenName(screenName);
    mTracker.send(builder.build());
  }
}
