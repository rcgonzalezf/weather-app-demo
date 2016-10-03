package rcgonzalezf.org.weather;

import android.app.Application;
import android.content.Context;
import com.facebook.stetho.Stetho;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

  private void givenDebugMode() {
    WeatherApp.sIsDebugMode = true;
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