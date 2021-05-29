package rcgonzalezf.org.weather.utils

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import androidx.annotation.VisibleForTesting
import rcgonzalezf.org.weather.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Helper methods to format strings or do misc stuff
 * I found all of them in different sources, but
 * specially I used some of them for my assignment 3 of one of courses from Coursera
 *
 * https://github.com/juleswhite/mobilecloud-15/tree/master/ex/WeatherServiceProvider
 */
object WeatherUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    @JvmStatic
    fun hasInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    @JvmStatic
    @JvmOverloads
    @VisibleForTesting
    fun getDayName(context: Context, dateStr: String?, todayDate: Date = Date()): String? {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return try {
            val inputDate = dateFormat.parse(dateStr)
            if (isSameDay(inputDate, todayDate)) {
                context.getString(R.string.today)
            } else {
                val cal = Calendar.getInstance()
                cal.time = todayDate
                cal.add(Calendar.DATE, 1)
                val tomorrowDate = cal.time
                if (isSameDay(inputDate, tomorrowDate)) {
                    context.getString(R.string.tomorrow)
                } else {
                    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    dayFormat.format(inputDate)
                }
            }
        } catch (e: ParseException) {
            dateStr
        }
    }

    /**
     *
     * Checks if two dates are on the same day ignoring time.
     *
     * @param date1 the first date, not altered, not null
     * @param date2 the second date, not altered, not null
     * @return true if they represent the same day
     */
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isSameDay(cal1, cal2)
    }

    /**
     *
     * Checks if two calendars represent the same day ignoring time.
     *
     * @param cal1 the first calendar, not altered, not null
     * @param cal2 the second calendar, not altered, not null
     * @return true if they represent the same day
     */
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return (cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
                && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR])
    }

    @JvmStatic
    fun formatDate(dateStr: String): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return try {
            val inputDate = dateFormat.parse(dateStr)
            val dayFormat = SimpleDateFormat("MMM, dd HH:mm", Locale.getDefault())
            dayFormat.format(inputDate)
        } catch (e: ParseException) {
            dateStr
        }
    }

    @JvmStatic
    fun formatTemperature(temperature: Double, isCelsius: Boolean, symbol: String?): String {
        var temp = temperature
        temp = if (!isCelsius) // Conversion of Kelvin to Fahrenheit temperature.
        {
            1.8 * (temp - 273) + 32
        } else  // Conversion of Kelvin to Celsius temperature.
        {
            temp - 273
        }
        return String.format(Locale.getDefault(), "%1.0f\u00B0 %2\$s", temp, symbol)
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    @JvmStatic
    fun isXLargeTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
    }
}
