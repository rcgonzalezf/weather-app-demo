package rcgonzalezf.org.weather.common.analytics;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.WeatherApp;

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

  private AnalyticsObserver mAnalyticsTestObserver = new AnalyticsObserver() {
    @Override public void onScreen(String screenName, AnalyticsBaseData analyticsBaseData) {
      mHandlingOnScreen = true;
      mAnalyticsBaseData = analyticsBaseData;
    }

    @Override public void onAction(AnalyticsEvent analyticsEvent) {
      mHandlingOnAction = true;
      mSomeAnalyticsEvent = analyticsEvent;
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

    thenTestObserverNotifiedOnScreen();
    thenSecondObserverNotifiedScreen();
  }

  @Test public void shouldNotifyOnAction() {
    givenAction();
    givenAndroidVersion("5.0");

    whenNotifyingOnAction();

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "5.0");
    thenOnScreenNotified();
    thenOnActionNotified();
  }

  private void givenAppVersion(String appVersion) {
    AnalyticsManager.sAppVersion = appVersion;
  }
  
  private void givenAndroidVersion(String androidSdkVersion) {
    AnalyticsManager.sAndroidVersion = androidSdkVersion;
  }

}
