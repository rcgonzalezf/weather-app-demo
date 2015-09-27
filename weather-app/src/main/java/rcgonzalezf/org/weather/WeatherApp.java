package rcgonzalezf.org.weather;

import org.rcgonzalezf.weather.WeatherLibApp;

public class WeatherApp extends WeatherLibApp {

  @Override public void onCreate() {
    super.onCreate();
    setAppInstance(this);
  }
}
