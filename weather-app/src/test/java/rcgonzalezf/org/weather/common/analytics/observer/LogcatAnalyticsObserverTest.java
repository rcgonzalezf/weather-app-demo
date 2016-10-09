package rcgonzalezf.org.weather.common.analytics.observer;

import android.util.Log;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.common.analytics.AnalyticsBaseData;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;

@RunWith(JMockit.class) public class LogcatAnalyticsObserverTest {

  @Tested private LogcatAnalyticsObserver uut;

  @Mocked private Log logger;
  private String mScreenName;
  private AnalyticsBaseData mAnalyticsBaseData;
  private AnalyticsEvent mAnalyticsEvent;

  @Before public void setUp() throws Exception {
    uut = new LogcatAnalyticsObserver();
  }

  @Test public void shouldHandleOnScreen() {
    givenScreenName("test");
    givenAnalyticsBaseData();

    whenHandlingOnScreen();

    thenShouldLogData();
  }

  @Test public void shouldHandleOnAction() {
    givenAnalyticsEvent();
    givenScreenName("test");
    givenAnalyticsBaseData();

    whenHandlingOnAction();

    thenShouldLogData();
  }

  private void givenAnalyticsEvent() {
    mAnalyticsEvent = new AnalyticsEvent();
    mAnalyticsEvent.name = "button pressed";
    mAnalyticsEvent.additionalValue = "testA";
  }

  private void whenHandlingOnAction() {
    uut.onAction(mAnalyticsEvent, mScreenName, mAnalyticsBaseData);
  }

  private void thenShouldLogData() {
    new Verifications() {{
      Log.d(LogcatAnalyticsObserver.class.getName(), withAny("string"));
    }};
  }

  private void whenHandlingOnScreen() {
    uut.onScreen(mScreenName, mAnalyticsBaseData);
  }

  private void givenAnalyticsBaseData() {
    mAnalyticsBaseData = new AnalyticsBaseData();
    mAnalyticsBaseData.data().put("OS", "TestOS");
    mAnalyticsBaseData.data().put("MULTIPANE", "true");
  }

  private void givenScreenName(String screenName) {
    mScreenName = screenName;
  }
}
