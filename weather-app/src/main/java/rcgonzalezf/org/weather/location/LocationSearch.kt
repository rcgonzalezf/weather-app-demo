package rcgonzalezf.org.weather.location

interface LocationSearch {
    fun searchByLatLon(lat: Double, lon: Double)
}
