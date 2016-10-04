package rcgonzalezf.org.weather;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import io.fabric.sdk.android.Fabric;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class) public class WeatherAppTest {

  @Tested private WeatherApp uut;

  @SuppressWarnings("unused") @Mocked private Application mApplication;
  @Mocked Crashlytics mCrashlytics;
  @Mocked Fabric mFabric;

  private boolean mStethoInitialized;
  private boolean mIsDebugModeOriginal;
  private OkHttpClient mOkHttpClient;

  @Before public void setUpApplication() throws Exception {

    new MockUp<Stetho>() {
      @SuppressWarnings("unused") @Mock void initializeWithDefaults(final Context context) {
        mStethoInitialized = true;
      }
    };

    mIsDebugModeOriginal = WeatherApp.sIsDebugMode;
    uut = new WeatherApp();
  }

  @After public void resetIsDebugMode() {
    WeatherApp.sIsDebugMode = mIsDebugModeOriginal;
  }

  @Test public void shouldSetTheAppInstanceOnCreatingTheApplication() {

    whenCreatingTheApplication();

    thenAppInstanceShouldNotBeNull();
  }

  @Test public void shouldInitializeStethoOnCreatingTheAppOnDebugMode() {

    givenDebugMode();

    whenCreatingTheApplication();

    thenStethoShouldBeInitialized(true);
    thenFabricShouldBeInitialized(false);
  }

  private void givenDebugMode() {
    WeatherApp.sIsDebugMode = true;
  }

  @Test public void shouldNotInitializeStethoOnCreatingTheAppIfNotDebug() {

    givenIsNotDebugMode();

    whenCreatingTheApplication();

    thenStethoShouldBeInitialized(false);
  }

  @Test public void shouldInitializeFabricIfTheAppNotInDebug() {
    givenIsNotDebugMode();

    whenCreatingTheApplication();

    thenFabricShouldBeInitialized(true);
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
    assertTrue(mOkHttpClient.interceptors().isEmpty());
  }

  private void thenShouldAddTheInterceptor() {
    assertFalse(mOkHttpClient.interceptors().isEmpty());
  }

  private void whenCreatingTheOkHttpClient() {
    mOkHttpClient = uut.createOkHttpClient();
  }

  private void givenIsNotDebugMode() {
    WeatherApp.sIsDebugMode = false;
  }

  private void thenStethoShouldBeInitialized(boolean expected) {
    assertEquals(expected, mStethoInitialized);
  }

  private void thenFabricShouldBeInitialized(boolean initialized) {
    if(initialized) {
      new Verifications() {{
        Fabric.with(uut, new Crashlytics());
      }};
    }
  }


  private void thenAppInstanceShouldNotBeNull() {
    assertNotNull(WeatherApp.getInstance());
  }

  private void whenCreatingTheApplication() {
    uut.onCreate();
  }
}