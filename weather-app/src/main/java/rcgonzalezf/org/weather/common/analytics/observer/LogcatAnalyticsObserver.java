package rcgonzalezf.org.weather.common.analytics.observer;

import android.support.annotation.NonNull;
import android.util.Log;
import java.util.Map;
import rcgonzalezf.org.weather.common.analytics.AnalyticsBaseData;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;
import rcgonzalezf.org.weather.common.analytics.AnalyticsObserver;

public class LogcatAnalyticsObserver implements AnalyticsObserver {

  private static String TAG = LogcatAnalyticsObserver.class.getName();

  @Override
  public void onScreen(@NonNull String screenName, @NonNull AnalyticsBaseData analyticsBaseData) {
    String builder = "onScreen: [ " +
        screenName +
        " | data: " +
        printableData(analyticsBaseData.data()) +
        " ]";

    Log.d(TAG, builder);
  }

  @Override public void onAction(@NonNull AnalyticsEvent analyticsEvent, @NonNull String screenName,
      @NonNull AnalyticsBaseData analyticsBaseData) {
    String builder = "onAction: [ event:" +
        " name=" + analyticsEvent.name +
        " additionalValue=" + analyticsEvent.additionalValue +
        " | screenName, " +
        screenName +
        " | data: " +
        printableData(analyticsBaseData.data()) +
        " ]";
    Log.d(TAG, builder);
  }

  private StringBuilder printableData(Map<String, String> data) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String key : data.keySet()) {
      stringBuilder.append("{").append(key).append(" | ").append(data.get(key)).append("}");
    }
    return stringBuilder;
  }
}
