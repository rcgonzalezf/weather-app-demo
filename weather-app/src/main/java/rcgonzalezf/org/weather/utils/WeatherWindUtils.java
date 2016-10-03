package rcgonzalezf.org.weather.utils;

import android.content.Context;
import rcgonzalezf.org.weather.R;

public class WeatherWindUtils {

  public static String getFormattedWind(Context context, double windSpeedStr, double windDirStr) {
    String windFormat = "%1$s: %2$s km/h %3$s";

    // From wind direction in degrees, determine compass direction
    // as a string (e.g., NW).
    String direction;
    if (windDirStr >= 22.5 && windDirStr < 67.5) {
      direction = "NE";
    } else if (windDirStr >= 67.5 && windDirStr < 112.5) {
      direction = "E";
    } else if (windDirStr >= 112.5 && windDirStr < 157.5) {
      direction = "SE";
    } else if (windDirStr >= 157.5 && windDirStr < 202.5) {
      direction = "S";
    } else if (windDirStr >= 202.5 && windDirStr < 247.5) {
      direction = "SW";
    } else if (windDirStr >= 247.5 && windDirStr < 292.5) {
      direction = "W";
    } else if (windDirStr >= 292.5 && windDirStr < 337.5) {
      direction = "NW";
    } else if (windDirStr >= 337.5) {
      direction = "N";
    } else {
      direction = "N";
    }

    return String.format(windFormat, context.getString(R.string.wind), windSpeedStr, direction);
  }
}
