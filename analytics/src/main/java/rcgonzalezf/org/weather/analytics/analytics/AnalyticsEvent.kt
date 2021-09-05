package rcgonzalezf.org.weather.analytics.analytics

data class AnalyticsEvent(@param:AnalyticsCatalog var name: String,
                          var additionalValue: String? = null)
