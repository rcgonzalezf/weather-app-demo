package rcgonzalezf.org.weather.common.analytics;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsManager.MOBILE;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsManager.NONE;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsManager.UNKNOWN;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsManager.WIFI;

@StringDef({ MOBILE, UNKNOWN, NONE, WIFI }) @Retention(RetentionPolicy.SOURCE) @interface NetworkType {
}
