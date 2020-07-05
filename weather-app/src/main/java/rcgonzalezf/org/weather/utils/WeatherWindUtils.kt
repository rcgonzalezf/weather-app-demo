package rcgonzalezf.org.weather.utils

import android.content.Context
import rcgonzalezf.org.weather.R

object WeatherWindUtils {
    @JvmStatic
    fun getFormattedWind(context: Context, windSpeedStr: Double, windDirStr: Double): String {
        val windFormat = "%1\$s: %2\$s km/h %3\$s"

        // From wind direction in degrees, determine compass direction
        // as a string (e.g., NW).
        val direction: String = if (windDirStr >= 22.5 && windDirStr < 67.5) {
            "NE"
        } else if (windDirStr >= 67.5 && windDirStr < 112.5) {
            "E"
        } else if (windDirStr >= 112.5 && windDirStr < 157.5) {
            "SE"
        } else if (windDirStr >= 157.5 && windDirStr < 202.5) {
            "S"
        } else if (windDirStr >= 202.5 && windDirStr < 247.5) {
            "SW"
        } else if (windDirStr >= 247.5 && windDirStr < 292.5) {
            "W"
        } else if (windDirStr >= 292.5 && windDirStr < 337.5) {
            "NW"
        } else if (windDirStr >= 337.5) {
            "N"
        } else {
            "N"
        }
        return String.format(windFormat, context.getString(R.string.wind), windSpeedStr, direction)
    }
}
