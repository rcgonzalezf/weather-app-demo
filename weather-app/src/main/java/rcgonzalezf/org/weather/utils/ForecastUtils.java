package rcgonzalezf.org.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import rcgonzalezf.org.weather.R;

/**
 * Helper methods to format strings or do misc stuff
 * I found all of them in different sources, but
 * specially I used some of them for my assignment 3 of one of courses from Coursera
 *
 * https://github.com/juleswhite/mobilecloud-15/tree/master/ex/WeatherServiceProvider
 */
public class ForecastUtils {

  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static boolean hasInternetConnection(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }

  public static int getArtResourceForWeatherCondition(int weatherId) {
    // Based on weather code data found at:
    // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
    if (weatherId >= 200 && weatherId <= 232) {
      return R.drawable.art_storm;
    } else if (weatherId >= 300 && weatherId <= 321) {
      return R.drawable.art_light_rain;
    } else if (weatherId >= 500 && weatherId <= 504) {
      return R.drawable.art_rain;
    } else if (weatherId == 511) {
      return R.drawable.art_snow;
    } else if (weatherId >= 520 && weatherId <= 531) {
      return R.drawable.art_rain;
    } else if (weatherId >= 600 && weatherId <= 622) {
      return R.drawable.art_snow;
    } else if (weatherId >= 701 && weatherId <= 761) {
      return R.drawable.art_fog;
    } else if (weatherId == 761 || weatherId == 781) {
      return R.drawable.art_storm;
    } else if (weatherId == 800) {
      return R.drawable.art_clear;
    } else if (weatherId == 801) {
      return R.drawable.art_light_clouds;
    } else if (weatherId >= 802 && weatherId <= 804) return R.drawable.art_clouds;

    return -1;
  }

  public static String getDayName(Context context, String dateStr) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    try {
      Date inputDate = dateFormat.parse(dateStr);
      Date todayDate = new Date();

      if (todayDate.equals(inputDate)) {
        return context.getString(R.string.today);
      } else {
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DATE, 1);
        Date tomorrowDate = cal.getTime();
        if (tomorrowDate.equals(inputDate)) {
          return context.getString(R.string.tomorrow);
        } else {
          SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
          return dayFormat.format(inputDate);
        }
      }
    } catch (ParseException e) {
      return dateStr;
    }
  }

  public static String formatDate(String dateStr) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    try {
      Date inputDate = dateFormat.parse(dateStr);
      SimpleDateFormat dayFormat = new SimpleDateFormat("MMM, dd HH:mm", Locale.getDefault());
      return dayFormat.format(inputDate);
    } catch (ParseException e) {
      return dateStr;
    }
  }

  public static String getFormattedWind(Context context, double windSpeedStr, double windDirStr) {
    String windFormat = "%1$s: %2$s km/h %3$s";

    // From wind direction in degrees, determine compass direction
    // as a string (e.g., NW).
    String direction = "Unknown";
    if (windDirStr >= 337.5 || windDirStr < 22.5) {
      direction = "N";
    } else if (windDirStr >= 22.5 && windDirStr < 67.5) {
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
    } else if (windDirStr >= 292.5 && windDirStr < 337.5) direction = "NW";

    return String.format(windFormat, context.getString(R.string.wind), windSpeedStr, direction);
  }

  public static String formatTemperature(double temperature, boolean isFahrenheit, String symbol) {
    if (isFahrenheit)
    // Conversion of Kelvin to Fahrenheit temperature.
    {
      temperature = 1.8 * (temperature - 273) + 32;
    } else
    // Conversion of Kelvin to Celsius temperature.
    {
      temperature = temperature - 273;
    }

    return String.format("%1.0f\u00B0 %2$s", temperature, symbol);
  }
}
