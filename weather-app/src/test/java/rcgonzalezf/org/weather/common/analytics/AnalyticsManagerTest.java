package rcgonzalezf.org.weather.common.analytics;

import mockit.Expectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.WeatherApp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JMockit.class)
public class AnalyticsManagerTest {

  @Tested
  private AnalyticsManager uut;

  private boolean mHandlingOnScreen;
  private boolean mHandlingOnAction;
  private AnalyticsEvent mSomeAnalyticsEvent;

  private AnalyticsBaseData mAnalyticsBaseData;
  private String mOriginalAndroidVersion;
  private String mOriginalAppVersion;
  private AnalyticsObserver mSecondMockObserver;
  private String mAction;
  private String mScreen;

  private AnalyticsObserver mAnalyticsTestObserver = new AnalyticsObserver() {
    @Override public void onScreen(String screenName, AnalyticsBaseData analyticsBaseData) {
      mHandlingOnScreen = true;
      mAnalyticsBaseData = analyticsBaseData;
      mScreen = screenName;
    }

    @Override public void onAction(AnalyticsEvent analyticsEvent, String screenName, AnalyticsBaseData analyticsBaseData) {
      mHandlingOnAction = true;
      mSomeAnalyticsEvent = analyticsEvent;
      mScreen = screenName;
    }
  };

  @Before public void initAnalyticsManager() {
    uut = WeatherApp.getAnalyticsManager();
    uut.addObserver(mAnalyticsTestObserver);
    mOriginalAndroidVersion = AnalyticsManager.sAndroidVersion;
    mOriginalAppVersion = AnalyticsManager.sAppVersion;
  }

  @After public void resetValues() {
    AnalyticsManager.sAndroidVersion = mOriginalAndroidVersion;
    AnalyticsManager.sAppVersion = mOriginalAppVersion;
  }

  @Test public void shouldContainBaseData() {
    givenAndroidVersion("6.0");
    givenAppVersion("0.4");
    givenNetwork("Wifi");
    givenIsMultipane(true);

    whenNotifyOnScreen("test");

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "6.0");
    thenAnalyticsDataShouldContain(AnalyticsManager.APP_VERSION, "0.4");
    thenAnalyticsDataShouldContain(AnalyticsManager.NETWORK, "Wifi");
    thenAnalyticsDataShouldContain(AnalyticsManager.MULTIPANE, "true");
  }

  @Test public void shouldNotifyMultipleObservers() {
    givenCleanObserver();
    givenObserver();
    givenSecondObserver();

    whenNotifyOnScreen("test");

    thenOnScreenNotified();
    thenScreenNotEmpty();
    thenSecondObserverNotifiedScreen();
  }

  @Test public void shouldNotifyOnAction() {
    givenAction("buttonPressed");
    givenAndroidVersion("5.0");

    whenNotifyingOnAction();

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "5.0");
    thenScreenNotEmpty();
    thenOnActionNotified();
  }

  private void thenScreenNotEmpty() {
    assertNotNull(mScreen);
  }

  private void givenCleanObserver() {
    uut.removeObserver(mAnalyticsTestObserver);
  }

  private void thenOnActionNotified() {
    assertTrue(mHandlingOnScreen);
  }

  private void thenOnScreenNotified() {
    assertTrue(mHandlingOnScreen);
  }

  private void whenNotifyingOnAction() {
    uut.notifyOnAction(mAction);
  }

  private void givenAction(String action) {
    mAction = action;
  }

  private void givenAppVersion(String appVersion) {
    AnalyticsManager.sAppVersion = appVersion;
  }

  private void givenAndroidVersion(String androidSdkVersion) {
    AnalyticsManager.sAndroidVersion = androidSdkVersion;
  }

  private void thenAnalyticsDataShouldContain(String key, String value) {
    assertNotNull(mAnalyticsBaseData.data().containsKey(key));
    assertEquals(value, mAnalyticsBaseData.data().get(key));
  }

  private void whenNotifyOnScreen(String screenName) {
    uut.notifyOnScreenLoad(screenName);
  }

  private void givenIsMultipane(final boolean isMultipane) {
    new Expectations() {{
      uut.isMultipane(); result = isMultipane;
    }};
  }

  private void givenNetwork(final String networkType) {
    new Expectations() {{
      uut.getNetworkType(); result = networkType;
    }};
  }

  private void thenSecondObserverNotifiedScreen() {
    verify(mSecondMockObserver, times(1)).onScreen(anyString(), any(AnalyticsBaseData.class));
  }

  private void givenSecondObserver() {
    mSecondMockObserver = mock(AnalyticsObserver.class);
    uut.addObserver(mSecondMockObserver);
  }

  private void givenObserver() {
    uut.addObserver(mAnalyticsTestObserver);
  }

}
