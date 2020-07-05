package rcgonzalezf.org.weather.location

interface LocationRetrieverListener {
    fun checkForPermissions()
    fun onEmptyLocation()
    fun onLocationFound(lat: Double, lon: Double)
}
