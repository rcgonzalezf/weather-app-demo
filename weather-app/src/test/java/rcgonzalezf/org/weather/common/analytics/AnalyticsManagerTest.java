package rcgonzalezf.org.weather.common.analytics;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.facebook.stetho.Stetho;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.utils.WeatherUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JMockit.class) public class AnalyticsManagerTest {

  private AnalyticsManager uut;

  @SuppressWarnings("unused") @Mocked private Stetho stetho;
  @SuppressWarnings("unused") @Mocked private ConnectivityManager connectivityManager;
  @SuppressWarnings("unused") @Mocked private Configuration configuration;
  @SuppressWarnings("unused") @Mocked private Context context;
  @SuppressWarnings("unused") @Mocked private NetworkInfo networkInfo;

  private boolean handlingOnScreen;

  private AnalyticsBaseData analyticsBaseData;
  private String originalAndroidVersion;
  private String originalAppVersion;
  private AnalyticsObserver secondMockObserver;
  private String action;
  private String screen;

  private AnalyticsObserver mAnalyticsTestObserver = new AnalyticsObserver() {
    @Override public void onScreen(String screenName, AnalyticsBaseData analyticsBaseData) {
      handlingOnScreen = true;
      AnalyticsManagerTest.this.analyticsBaseData = analyticsBaseData;
      screen = screenName;
    }

    @Override public void onAction(AnalyticsEvent analyticsEvent, String screenName,
        AnalyticsBaseData analyticsBaseData) {
      screen = screenName;
    }
  };

  @Before public void initWeatherApp() {
    new Expectations() {{
      context.getSystemService(Context.CONNECTIVITY_SERVICE);
      result = connectivityManager;
    }};

    originalAndroidVersion = AnalyticsManager.BUILD_ANDROID_VERSION;
    originalAppVersion = AnalyticsManager.BUILD_APP_VERSION;

    if (originalAndroidVersion == null) {
      AnalyticsManager.BUILD_ANDROID_VERSION = "";
      AnalyticsManager.BUILD_APP_VERSION = "";
    }
    uut = new AnalyticsManager(context);
    uut.addObserver(mAnalyticsTestObserver);
  }

  @After public void resetValues() {
    AnalyticsManager.BUILD_ANDROID_VERSION = originalAndroidVersion;
    AnalyticsManager.BUILD_APP_VERSION = originalAppVersion;
  }

  @Test public void shouldContainBaseDataWithWifi(
      @SuppressWarnings("UnusedParameters") @Mocked WeatherUtils weatherUtils) {
    givenAndroidVersion("6.0");
    givenAppVersion("0.4");
    givenIsMultipane(true);
    givenNetwork(ConnectivityManager.TYPE_WIFI);
    givenUut();

    whenNotifyOnScreen("test");

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "6.0");
    thenAnalyticsDataShouldContain(AnalyticsManager.APP_VERSION, "0.4");
    thenAnalyticsDataShouldContain(AnalyticsManager.NETWORK, "Wifi");
    thenAnalyticsDataShouldContain(AnalyticsManager.MULTIPANE, "true");
  }

  @Test public void shouldContainBaseDataWithMobileNetwork(
      @SuppressWarnings("UnusedParameters") @Mocked WeatherUtils weatherUtils) {
    givenAndroidVersion("6.0");
    givenAppVersion("0.4");
    givenIsMultipane(true);
    givenNetwork(ConnectivityManager.TYPE_MOBILE);
    givenUut();

    whenNotifyOnScreen("test");

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "6.0");
    thenAnalyticsDataShouldContain(AnalyticsManager.APP_VERSION, "0.4");
    thenAnalyticsDataShouldContain(AnalyticsManager.NETWORK, "Mobile");
    thenAnalyticsDataShouldContain(AnalyticsManager.MULTIPANE, "true");
  }

  @Test public void shouldContainBaseDataWithUnknownNetwork(
      @SuppressWarnings("UnusedParameters") @Mocked WeatherUtils weatherUtils) {
    givenAndroidVersion("6.0");
    givenAppVersion("0.4");
    givenIsMultipane(true);
    givenNetwork(ConnectivityManager.TYPE_DUMMY);
    givenUut();

    whenNotifyOnScreen("test");

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "6.0");
    thenAnalyticsDataShouldContain(AnalyticsManager.APP_VERSION, "0.4");
    thenAnalyticsDataShouldContain(AnalyticsManager.NETWORK, "Unknown");
    thenAnalyticsDataShouldContain(AnalyticsManager.MULTIPANE, "true");
  }

  @Test public void shouldContainBaseDataWithNoNetwork(
      @SuppressWarnings("UnusedParameters") @Mocked WeatherUtils weatherUtils) {
    givenAndroidVersion("6.0");
    givenAppVersion("0.4");
    givenIsMultipane(true);
    givenNullNetwork();
    givenUut();

    whenNotifyOnScreen("test");

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "6.0");
    thenAnalyticsDataShouldContain(AnalyticsManager.APP_VERSION, "0.4");
    thenAnalyticsDataShouldContain(AnalyticsManager.NETWORK, "None");
    thenAnalyticsDataShouldContain(AnalyticsManager.MULTIPANE, "true");
  }

  @Test public void shouldNotifyMultipleObservers() {
    givenPreviousObserverRemoval();
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
    givenUut();
    givenScreenNotified("test");

    whenNotifyingOnAction();

    thenAnalyticsDataShouldContain(AnalyticsManager.ANDROID_VERSION, "5.0");
    thenScreenNotEmpty();
    thenOnActionNotified();
  }

  private void givenScreenNotified(String screenName) {
    uut.notifyOnScreenLoad(screenName);
  }

  private void givenUut() {
    uut = new AnalyticsManager(context);
    uut.addObserver(mAnalyticsTestObserver);
  }

  private void thenScreenNotEmpty() {
    assertNotNull(screen);
  }

  private void givenPreviousObserverRemoval() {
    uut.removeObserver(mAnalyticsTestObserver);
  }

  private void thenOnActionNotified() {
    assertTrue(handlingOnScreen);
  }

  private void thenOnScreenNotified() {
    assertTrue(handlingOnScreen);
  }

  private void whenNotifyingOnAction() {
    AnalyticsEvent analyticsEvent = new AnalyticsEvent(action, null);
    uut.notifyOnAction(analyticsEvent);
  }

  private void givenAction(String action) {
    this.action = action;
  }

  private void givenAppVersion(String appVersion) {
    AnalyticsManager.BUILD_APP_VERSION = appVersion;
  }

  private void givenAndroidVersion(String androidSdkVersion) {
    AnalyticsManager.BUILD_ANDROID_VERSION = androidSdkVersion;
  }

  private void thenAnalyticsDataShouldContain(String key, String value) {
    assertNotNull(analyticsBaseData.data().containsKey(key));
    assertEquals(value, analyticsBaseData.data().get(key));
  }

  private void whenNotifyOnScreen(String screenName) {
    uut.notifyOnScreenLoad(screenName);
  }

  private void givenIsMultipane(final boolean isMultipane) {
    new Expectations() {{
      WeatherUtils.isXLargeTablet(context);
      result = isMultipane;
    }};
  }

  private void givenNetwork(final int connectivityManagerType) {
    new Expectations() {{
      networkInfo.getType();
      result = connectivityManagerType;
    }};
  }

  private void givenNullNetwork() {
    new Expectations() {{
      connectivityManager.getActiveNetworkInfo();
      result = null;
    }};
  }

  private void thenSecondObserverNotifiedScreen() {
    verify(secondMockObserver, times(1)).onScreen(anyString(), any(AnalyticsBaseData.class));
  }

  private void givenSecondObserver() {
    secondMockObserver = mock(AnalyticsObserver.class);
    uut.addObserver(secondMockObserver);
  }

  private void givenObserver() {
    uut.addObserver(mAnalyticsTestObserver);
  }
}
