package rcgonzalezf.org.weather.common.analytics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsTest {

  private Analytics uut;
  @SuppressWarnings("unused")
  @Mock
  private AnalyticsManager analyticsManager;
  private String screenName;
  private AnalyticsEvent analyticsEvent;

  @Before public void setUp() {
    uut = new Analytics(analyticsManager);
  }

  @Test public void shouldTrackOnScreen() {
    givenScreenName("someScreen");

    whenTrackingOnScreen();

    thenAnalyticsManagerShouldNotifyOnScreen();
  }

  private void thenAnalyticsManagerShouldNotifyOnScreen() {
    Mockito.verify(analyticsManager, Mockito.atLeastOnce()).notifyOnScreenLoad(screenName);
  }

  @Test public void shouldTrackOnActionEvent() {
    givenAnalyticsEvent();

    whenTrackingOnActionEvent();

    thenAnalyticsManagerShouldNotifyOnAction();
  }

  private void thenAnalyticsManagerShouldNotifyOnAction() {
    Mockito.verify(analyticsManager, Mockito.atLeastOnce()).notifyOnAction(analyticsEvent);
  }

  private void whenTrackingOnActionEvent() {
    uut.trackOnActionEvent(analyticsEvent);
  }

  private void givenAnalyticsEvent() {
    analyticsEvent = new AnalyticsEvent(NO_NETWORK_SEARCH, null);
  }

  private void givenScreenName(String someScreen) {
    screenName = someScreen;
  }

  private void whenTrackingOnScreen() {
    uut.trackOnScreen(screenName);
  }
}
