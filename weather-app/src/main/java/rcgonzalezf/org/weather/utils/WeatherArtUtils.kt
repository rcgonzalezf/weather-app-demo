package rcgonzalezf.org.weather.utils

import androidx.annotation.DrawableRes
import rcgonzalezf.org.weather.R

object WeatherArtUtils {
    @JvmStatic
    @DrawableRes
    fun getArtResourceForWeatherCondition(weatherId: Int): Int {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId in 200..232) {
            return R.drawable.art_storm
        } else if (weatherId in 300..321) {
            return R.drawable.art_light_rain
        } else if (weatherId in 500..504) {
            return R.drawable.art_rain
        } else if (weatherId == 511) {
            return R.drawable.art_snow
        } else if (weatherId in 520..531) {
            return R.drawable.art_rain
        } else if (weatherId in 600..622) {
            return R.drawable.art_snow
        } else if (weatherId in 701..760) {
            return R.drawable.art_fog
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm
        } else if (weatherId == 800) {
            return R.drawable.art_clear
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds
        } else if (weatherId in 802..804) {
            return R.drawable.art_clouds
        }
        return -1
    }
}
