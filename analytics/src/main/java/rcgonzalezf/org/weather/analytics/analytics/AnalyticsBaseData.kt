package rcgonzalezf.org.weather.analytics.analytics

class AnalyticsBaseData {
    private val data: MutableMap<String, String> = mutableMapOf()
    fun data(): MutableMap<String, String> {
        return data
    }
}
