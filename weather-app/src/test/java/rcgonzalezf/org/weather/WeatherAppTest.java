package rcgonzalezf.org.weather;

import android.app.Application;
import android.content.Context;
import com.crittercism.app.Crittercism;
import com.facebook.stetho.Stetho;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class) public class WeatherAppTest {

  @Tested private WeatherApp uut;

  @SuppressWarnings("unused") @Mocked private Application application;

  private boolean stethoInitialized;
  private boolean isDebugModeOriginal;
  private OkHttpClient okHttpClient;
  @SuppressWarnings("unused") @Mocked AnalyticsManager analyticsManager;

  @Before public void setUpApplication() throws Exception {

    new MockUp<Stetho>() {
      @SuppressWarnings("unused") @Mock void initializeWithDefaults(final Context context) {
        stethoInitialized = true;
      }
    };

    isDebugModeOriginal = WeatherApp.sIsDebugMode;
    uut = new WeatherApp();
  }

  @After public void resetIsDebugMode() {
    WeatherApp.sIsDebugMode = isDebugModeOriginal;
  }

  @Test public void shouldSetTheAppInstanceOnCreatingTheApplication() {

    whenCreatingTheApplication();

    thenAppInstanceShouldNotBeNull();
  }

  @Test public void shouldInitializeStethoOnCreatingTheAppOnDebugMode() {

    givenDebugMode();

    whenCreatingTheApplication();

    thenStethoShouldBeInitialized(true);
  }

  @Test public void shouldInitializeCrittercismOnCreatingTheAppOnNonDebugMode(
      @SuppressWarnings("UnusedParameters") @Mocked Crittercism crittercism) {
    givenNonDebugMode();

    whenCreatingTheApplication();

    thenCrittercismShouldBeInitialized();
  }

  @Test public void shouldNotInitializeStethoOnCreatingTheAppIfNotDebug() {

    givenIsNotDebugMode();

    whenCreatingTheApplication();

    thenStethoShouldBeInitialized(false);
  }

  @Test public void shouldAddStethoInterceptorIfIsDebugMode() {
    givenDebugMode();

    whenCreatingTheOkHttpClient();

    thenShouldAddTheInterceptor();
  }

  @Test public void shouldNotHaveInterceptorsIfIsNotDebugMode() {
    givenIsNotDebugMode();

    whenCreatingTheOkHttpClient();

    thenShouldNotHaveInterceptors();
  }

  @Test public void shouldCreateNewAnalyticsManagerIfNoInstance() {
    givenAnalyticsManagerInstance();

    whenGettingAnalyticsManager();

    thenShouldCreateNewInstance();
  }

  private void givenAnalyticsManagerInstance() {
    WeatherApp.getAnalyticsManager();
  }

  private void thenShouldCreateNewInstance() {
    assertNotNull(WeatherApp.getAnalyticsManager());
  }

  private void whenGettingAnalyticsManager() {
    WeatherApp.getAnalyticsManager();
  }

  private void thenCrittercismShouldBeInitialized() {
    new Verifications() {{
      Crittercism.initialize(withAny(uut), withAny("apiKey"));
    }};
  }

  private void givenNonDebugMode() {
    WeatherApp.sIsDebugMode = false;
  }

  private void givenDebugMode() {
    WeatherApp.sIsDebugMode = true;
  }

  private void thenShouldNotHaveInterceptors() {
    assertTrue(okHttpClient.interceptors().isEmpty());
  }

  private void thenShouldAddTheInterceptor() {
    assertFalse(okHttpClient.interceptors().isEmpty());
  }

  private void whenCreatingTheOkHttpClient() {
    okHttpClient = uut.createOkHttpClient();
  }

  private void givenIsNotDebugMode() {
    WeatherApp.sIsDebugMode = false;
  }

  private void thenStethoShouldBeInitialized(boolean expected) {
    assertEquals(expected, stethoInitialized);
  }

  private void thenAppInstanceShouldNotBeNull() {
    assertNotNull(WeatherApp.getInstance());
  }

  private void whenCreatingTheApplication() {
    uut.onCreate();
  }
}