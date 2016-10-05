package rcgonzalezf.org.weather;

import android.support.annotation.VisibleForTesting;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.WeatherLibApp;

public class WeatherApp extends WeatherLibApp {

  @VisibleForTesting static boolean sIsDebugMode = BuildConfig.DEBUG;

  @Override public void onCreate() {
    super.onCreate();

    if (sIsDebugMode) {
      Stetho.initializeWithDefaults(this);
    } else {
      Fabric.with(this, new Crashlytics());
    }

    setAppInstance(this);
  }

  @Override public OkHttpClient createOkHttpClient() {

    OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

    if (sIsDebugMode) {
      okHttpBuilder.addInterceptor(new StethoInterceptor());
    }

    return okHttpBuilder.build();
  }
}
