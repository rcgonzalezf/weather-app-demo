package rcgonzalezf.org.weather.location

interface CityFromLatLongRetriever {
    fun getFromLatLong(lat: Double, lon: Double): String?
}
