package org.rcgonzalezf.weather;

import android.app.Application;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import java.io.File;
import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherProvider;

public abstract class WeatherLibApp extends Application {

  private static final String TAG = WeatherLibApp.class.getSimpleName();

  private static WeatherLibApp sAppInstance = null;

  @Override public void onCreate() {
    super.onCreate();
    ServiceConfig serviceConfig = ServiceConfig.getInstance();
    serviceConfig.setApiKey(getString(R.string.open_weather_map_api_key));
    serviceConfig.setWeatherProvider(WeatherProvider.OpenWeather);

    enableHttpResponseCache();
    addAnalyticsObservers();
  }

  public static WeatherLibApp getInstance() {
    return sAppInstance;
  }

  public static void setAppInstance(WeatherLibApp appInstance) {
    sAppInstance = appInstance;
  }

  @VisibleForTesting
  void enableHttpResponseCache() {
    try {
      long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
      File httpCacheDir = new File(getCacheDir(), "http");
      Class.forName("android.net.http.HttpResponseCache")
          .getMethod("install", File.class, long.class)
          .invoke(null, httpCacheDir, httpCacheSize);
    } catch (Exception httpResponseCacheNotAvailable) {
      Log.d(TAG, "HTTP response cache is unavailable.");
    }
  }

  public abstract OkHttpClient createOkHttpClient();

  public abstract void addAnalyticsObservers();
}
