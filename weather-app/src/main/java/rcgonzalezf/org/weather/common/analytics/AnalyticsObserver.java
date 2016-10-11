package rcgonzalezf.org.weather.common.analytics;

public interface AnalyticsObserver {

  void onScreen(String screenName, AnalyticsBaseData analyticsBaseData);

  void onAction(AnalyticsEvent analyticsEvent, String screenName, AnalyticsBaseData analyticsBaseData);
}
