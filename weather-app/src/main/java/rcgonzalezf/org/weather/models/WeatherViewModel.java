package rcgonzalezf.org.weather.models;

import android.os.Parcelable;

public interface WeatherViewModel extends Parcelable {

  int getId();

  String getCityName();

  double getSpeed();

  String getHumidity();

  String getTemperature();

  String getDateTime();

  int getWeatherId();

  String getCountry();

  double getDeg();
}
