package rcgonzalezf.org.weather.common.analytics

data class AnalyticsEvent(@param:AnalyticsCatalog var name: String,
                          var additionalValue: String? = null)
