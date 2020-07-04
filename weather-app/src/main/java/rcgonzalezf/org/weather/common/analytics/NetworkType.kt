package rcgonzalezf.org.weather.common.analytics

import androidx.annotation.StringDef
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.MOBILE
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.NONE
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.UNKNOWN
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.WIFI

@StringDef(MOBILE, UNKNOWN, NONE, WIFI)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
internal annotation class NetworkType
