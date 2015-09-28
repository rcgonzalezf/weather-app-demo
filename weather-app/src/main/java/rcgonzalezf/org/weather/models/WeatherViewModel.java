package rcgonzalezf.org.weather.models;

import android.os.Parcelable;
import java.io.Serializable;

public interface WeatherViewModel extends Parcelable, Serializable {

  int getId();

  String getCityName();

  double getSpeed();

  String getHumidity();

  double getTemperature();

  String getDateTime();

  int getWeatherId();

  String getCountry();

  double getDeg();

  String getDescription();
}
