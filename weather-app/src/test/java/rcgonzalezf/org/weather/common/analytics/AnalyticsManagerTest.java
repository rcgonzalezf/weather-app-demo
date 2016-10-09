package rcgonzalezf.org.weather.common.analytics;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.facebook.stetho.Stetho;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
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

  @Tested private AnalyticsManager uut;

  @SuppressWarnings("unused") @Mocked private Stetho mStetho;
  @SuppressWarnings("unused") @Mocked private ConnectivityManager mConnectivityManager;
  @SuppressWarnings("unused") @Mocked private Configuration mConfiguration;
  @SuppressWarnings("unused") @Mocked private Context mContext;
  @SuppressWarnings("unused") @Mocked private NetworkInfo mNetworkInfo;

  private boolean mHandlingOnScreen;

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

    @Override public void onAction(AnalyticsEvent analyticsEvent, String screenName,
        AnalyticsBaseData analyticsBaseData) {
      mScreen = screenName;
    }
  };

  @Before public void initWeatherApp() {
    new Expectations() {{
      mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      result = mConnectivityManager;
    }};

    mOriginalAndroidVersion = AnalyticsManager.sAndroidVersion;
    mOriginalAppVersion = AnalyticsManager.sAppVersion;
    uut = new AnalyticsManager(mContext);
    uut.addObserver(mAnalyticsTestObserver);
  }

  @After public void resetValues() {
    AnalyticsManager.sAndroidVersion = mOriginalAndroidVersion;
    AnalyticsManager.sAppVersion = mOriginalAppVersion;
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
    uut = new AnalyticsManager(mContext);
    uut.addObserver(mAnalyticsTestObserver);
  }

  private void thenScreenNotEmpty() {
    assertNotNull(mScreen);
  }

  private void givenPreviousObserverRemoval() {
    uut.removeObserver(mAnalyticsTestObserver);
  }

  private void thenOnActionNotified() {
    assertTrue(mHandlingOnScreen);
  }

  private void thenOnScreenNotified() {
    assertTrue(mHandlingOnScreen);
  }

  private void whenNotifyingOnAction() {
    AnalyticsEvent analyticsEvent = new AnalyticsEvent();
    analyticsEvent.name = mAction;
    uut.notifyOnAction(analyticsEvent);
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
      WeatherUtils.isXLargeTablet(mContext);
      result = isMultipane;
    }};
  }

  private void givenNetwork(final int connectivityManagerType) {
    new Expectations() {{
      mNetworkInfo.getType();
      result = connectivityManagerType;
    }};
  }

  private void givenNullNetwork() {
    new Expectations() {{
      mConnectivityManager.getActiveNetworkInfo();
      result = null;
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
