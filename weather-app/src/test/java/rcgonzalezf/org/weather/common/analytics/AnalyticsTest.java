package rcgonzalezf.org.weather.common.analytics;

import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH;

@RunWith(JMockit.class)
public class AnalyticsTest {

  @Tested
  private Analytics uut;
  @Mocked AnalyticsManager mAnalyticsManager;
  private String mScreenName;
  private AnalyticsEvent mAnalyticsEvent;

  @Before public void setUp() throws Exception {
    uut = new Analytics();
  }

  @Test public void shouldTrackOnScreen() throws Exception {
    givenScreenName("someScreen");

    whenTrackingOnScreen();

    thenAnalyticsManagerShouldNotifyOnScreen();
  }

  private void thenAnalyticsManagerShouldNotifyOnScreen() {
    new Verifications() {{
      mAnalyticsManager.notifyOnScreenLoad(mScreenName);
    }};
  }

  @Test public void shouldTrackOnActionEvent() throws Exception {
    givenAnalyticsEvent();

    whenTrackingOnActionEvent();

    thenAnalyticsManagerShouldNotifyOnAction();
  }

  private void thenAnalyticsManagerShouldNotifyOnAction() {
    new Verifications() {{
      mAnalyticsManager.notifyOnAction(mAnalyticsEvent);
    }};
  }

  private void whenTrackingOnActionEvent() {
    uut.trackOnActionEvent(mAnalyticsEvent);
  }

  private void givenAnalyticsEvent() {
    mAnalyticsEvent = new AnalyticsEvent(NO_NETWORK_SEARCH, null);
  }

  private void givenScreenName(String someScreen) {
    mScreenName = someScreen;
  }

  private void whenTrackingOnScreen() {
    uut.trackOnScreen(mScreenName);
  }

}