package rcgonzalezf.org.weather.common.analytics;

import androidx.annotation.NonNull;

import rcgonzalezf.org.weather.WeatherApp;

public class Analytics {

  public void trackOnScreen(String screenName) {
    WeatherApp.getAnalyticsManager().notifyOnScreenLoad(screenName);
  }

  public void trackOnActionEvent(@NonNull final AnalyticsEvent event) {
    WeatherApp.getAnalyticsManager().notifyOnAction(event);
  }
}
