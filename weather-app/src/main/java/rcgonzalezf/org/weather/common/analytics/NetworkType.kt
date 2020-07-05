package rcgonzalezf.org.weather.common.analytics

import androidx.annotation.StringDef
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.Companion.MOBILE
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.Companion.NONE
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.Companion.UNKNOWN
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager.Companion.WIFI

@StringDef(MOBILE, UNKNOWN, NONE, WIFI)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
internal annotation class NetworkType
