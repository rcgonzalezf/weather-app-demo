package rcgonzalezf.org.weather.location

import android.location.Address
import android.location.Geocoder
import android.util.Log

class GeoCodeRetriever(private val geoCoder: Geocoder) : CityFromLatLongRetriever {

    companion object {
        private val TAG = GeoCodeRetriever::class.java.simpleName
    }

    override fun getFromLatLong(lat: Double, lon: Double): String? {
        var cityName: String? = null
        val addresses: List<Address>
        try {
            addresses = geoCoder.getFromLocation(lat, lon, 1)
            cityName = addresses[0].locality
        } catch (e: Exception) {
            Log.d(TAG, "error retrieving the cityName with Geocoder")
        }
        return cityName
    }
}
