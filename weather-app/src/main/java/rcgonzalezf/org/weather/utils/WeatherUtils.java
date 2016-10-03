package rcgonzalezf.org.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
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
public class WeatherUtils {

  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static boolean hasInternetConnection(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }


  public static String getDayName(Context context, String dateStr) {
    Date todayDate = new Date();
   return getDayName(context, dateStr, todayDate);
  }

  @VisibleForTesting
  static String getDayName(Context context, String dateStr, Date todayDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    try {
      Date inputDate = dateFormat.parse(dateStr);
      if (isSameDay(inputDate, todayDate)) {
        return context.getString(R.string.today);
      } else {
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DATE, 1);
        Date tomorrowDate = cal.getTime();
        if (isSameDay(inputDate, tomorrowDate)) {
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

  /**
   * <p>Checks if two dates are on the same day ignoring time.</p>
   * @param date1  the first date, not altered, not null
   * @param date2  the second date, not altered, not null
   * @return true if they represent the same day
   */
  private static boolean isSameDay(@NonNull Date date1, @NonNull  Date date2) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);
    return isSameDay(cal1, cal2);
  }

  /**
   * <p>Checks if two calendars represent the same day ignoring time.</p>
   * @param cal1  the first calendar, not altered, not null
   * @param cal2  the second calendar, not altered, not null
   * @return true if they represent the same day
   */
  private static boolean isSameDay(@NonNull Calendar cal1, @NonNull Calendar cal2) {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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

  public static String formatTemperature(double temperature, boolean isFahrenheit, String symbol) {
    if (!isFahrenheit)
    // Conversion of Kelvin to Fahrenheit temperature.
    {
      temperature = 1.8 * (temperature - 273) + 32;
    } else
    // Conversion of Kelvin to Celsius temperature.
    {
      temperature = temperature - 273;
    }

    return String.format(Locale.getDefault(), "%1.0f\u00B0 %2$s", temperature, symbol);
  }
}
