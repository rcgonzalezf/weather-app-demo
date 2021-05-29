package rcgonzalezf.org.weather.common.analytics.observer;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import java.util.Map;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.common.analytics.AnalyticsBaseData;
import rcgonzalezf.org.weather.common.analytics.AnalyticsCatalog;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED;

@RunWith(JMockit.class) public class GoogleAnalyticsObserverTest {

  @Tested private GoogleAnalyticsObserver uut;

  @SuppressWarnings("unused") @Mocked private GoogleAnalytics mGoogleAnalytics;
  @SuppressWarnings("unused") @Mocked private Tracker mTracker;
  @SuppressWarnings("unused") @Mocked private HitBuilders mHitBuilders;
  private String mScreen;
  private AnalyticsBaseData mAnalyticsBaseData;
  private AnalyticsEvent mAnalyticsEvent;

  @Before public void setUp() throws Exception {
    uut = new GoogleAnalyticsObserver();
  }

  @Test public void shouldSendScreenViewToGoogleAnalyticsTracker(
      @Mocked HitBuilders.ScreenViewBuilder screenViewBuilder) throws Exception {
    givenScreen("testScreen");
    givenAnalyticsBaseData();

    whenHandlingOnScreen();

    thenTrackerShouldSetScreenName();
    thenTrackerShouldSendEventWith(screenViewBuilder.build());
  }

  @Test public void shouldSetActionAndCategoryForAnalyticsEvent(
      @Mocked HitBuilders.EventBuilder eventBuilder) throws Exception {
    givenScreen("testScreen");
    givenAnalyticsBaseData();
    givenAnalyticsEvent(SEARCH_COMPLETED, "testA");

    whenHandlingOnAction();

    thenBuilderShouldSetCategory(eventBuilder);
    thenBuilderShouldSetAction(eventBuilder);
    thenTrackerShouldSetScreenName();
    thenTrackerShouldSendEventWith(eventBuilder.build());
  }

  @Test public void shouldSetCategoryForAnalyticsEventWithoutAdditionalDetails(
      @Mocked HitBuilders.EventBuilder eventBuilder) throws Exception {
    givenScreen("testScreen");
    givenAnalyticsBaseData();
    givenAnalyticsEvent(SEARCH_COMPLETED, null);

    whenHandlingOnAction();

    thenBuilderShouldSetCategory(eventBuilder);
    thenTrackerShouldSetScreenName();
    thenTrackerShouldSendEventWith(eventBuilder.build());
  }

  @Test public void shouldAnonymizeIpOnCreatingTracker(
      @SuppressWarnings("UnusedParameters") @Mocked HitBuilders.ScreenViewBuilder screenViewBuilder)
      throws Exception {
    givenScreen("testScreen");
    givenAnalyticsBaseData();
    givenHandlingOnScreen();
    givenAnalyticsEvent(SEARCH_COMPLETED, null);

    whenHandlingOnAction();

    thenTrackerShouldAnonymizeIp();
  }

  private void thenTrackerShouldAnonymizeIp() {
    new Verifications() {{
      mTracker.setAnonymizeIp(true);
    }};
  }

  private void givenHandlingOnScreen() {
    uut.onScreen(mScreen, mAnalyticsBaseData);
  }

  private void thenBuilderShouldSetCategory(final HitBuilders.EventBuilder eventBuilder) {
    new Verifications() {{
      eventBuilder.setCategory(withEqual(mAnalyticsEvent.getName()));
    }};
  }

  private void thenBuilderShouldSetAction(final HitBuilders.EventBuilder eventBuilder) {
    new Verifications() {{
      eventBuilder.setAction(withEqual(mAnalyticsEvent.getAdditionalValue()));
    }};
  }

  private void whenHandlingOnAction() {
    uut.onAction(mAnalyticsEvent, mScreen, mAnalyticsBaseData);
  }

  private void givenAnalyticsEvent(@AnalyticsCatalog String name, String additionalValue) {
    mAnalyticsEvent = new AnalyticsEvent(name, additionalValue);
  }

  private void thenTrackerShouldSendEventWith(final Map<String, String> stringMap) {
    new Verifications() {{
      mTracker.send(withAny(stringMap));
    }};
  }

  private void thenTrackerShouldSetScreenName() {
    new Verifications() {{
      mTracker.setScreenName(withEqual(mScreen));
    }};
  }

  private void whenHandlingOnScreen() {
    uut.onScreen(mScreen, mAnalyticsBaseData);
  }

  private void givenAnalyticsBaseData() {
    mAnalyticsBaseData = new AnalyticsBaseData();
    mAnalyticsBaseData.data().put("OS", "TestOS");
    mAnalyticsBaseData.data().put("MULTIPANE", "true");
  }

  private void givenScreen(String screen) {
    mScreen = screen;
  }
}