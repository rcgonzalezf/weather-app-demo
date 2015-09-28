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

public class ForecastUtils {

  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static boolean hasInternetConnection(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null &&
        activeNetwork.isConnectedOrConnecting();
  }

  public static int getArtResourceForWeatherCondition(int weatherId) {
    // Based on weather code data found at:
    // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
    if (weatherId >= 200 && weatherId <= 232)
      return R.drawable.art_storm;
    else if (weatherId >= 300 && weatherId <= 321)
      return R.drawable.art_light_rain;
    else if (weatherId >= 500 && weatherId <= 504)
      return R.drawable.art_rain;
    else if (weatherId == 511)
      return R.drawable.art_snow;
    else if (weatherId >= 520 && weatherId <= 531)
      return R.drawable.art_rain;
    else if (weatherId >= 600 && weatherId <= 622)
      return R.drawable.art_snow;
    else if (weatherId >= 701 && weatherId <= 761)
      return R.drawable.art_fog;
    else if (weatherId == 761 || weatherId == 781)
      return R.drawable.art_storm;
    else if (weatherId == 800)
      return R.drawable.art_clear;
    else if (weatherId == 801)
      return R.drawable.art_light_clouds;
    else if (weatherId >= 802 && weatherId <= 804)
      return R.drawable.art_clouds;

    return -1;
  }

  public static String getDayName(Context context, String dateStr) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
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
        if (tomorrowDate.equals(
            dateStr)) {
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
}
