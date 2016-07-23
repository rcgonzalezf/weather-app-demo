package rcgonzalezf.org.weather;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.OkHttpClient;
import org.rcgonzalezf.weather.WeatherLibApp;

public class WeatherApp extends WeatherLibApp {

  @Override public void onCreate() {
    super.onCreate();
    setAppInstance(this);
  }

  @Override public OkHttpClient createOkHttpClient() {

    OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

    if (BuildConfig.DEBUG) {
      okHttpBuilder.addInterceptor(new StethoInterceptor());
    }

    return okHttpBuilder.build();
  }
}
