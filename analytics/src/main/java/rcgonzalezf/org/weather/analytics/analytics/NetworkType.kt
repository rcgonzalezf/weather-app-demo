package rcgonzalezf.org.weather.analytics.analytics

import androidx.annotation.StringDef
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager.Companion.MOBILE
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager.Companion.NONE
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager.Companion.UNKNOWN
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager.Companion.WIFI

@StringDef(MOBILE, UNKNOWN, NONE, WIFI)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
internal annotation class NetworkType
