package rcgonzalezf.org.weather.models;

import android.os.Parcelable;

public interface WeatherViewModel extends Parcelable {

  int getId();

  String getCityName();

  String getSpeed();

  String getHumidity();

  String getTemperature();

  String getDateTime();

  int getWeatherId();

  String getCountry();
}
